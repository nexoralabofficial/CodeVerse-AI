package com.example.ai

object CodeSenseParser {

  private val gccRegex = Regex("""([a-zA-Z0-9_\-./]+\.[a-zA-Z0-9]+):(\d+):(?:(\d+):)?\s*(error|warning|fatal error):\s*(.+)""")
  private val pyRegex = Regex("""File "([^"]+)", line (\d+)(?:, in .*)?\n(?:\s*(.+)\n)?([A-Za-z]+Error|Exception):\s*(.+)""")
  private val pySimpleRegex = Regex("""([A-Za-z]+Error|Exception):\s*(.+)""")
  private val javaRegex = Regex("""([a-zA-Z0-9_\-./]+\.java):(\d+):\s*(error|warning):\s*(.+)""")

  fun parseDiagnostics(
    sourceCode: String,
    fileName: String,
    compilerStderr: String,
    runtimeStderr: String
  ): List<CodeIssue> {
    val issues = mutableListOf<CodeIssue>()
    val fullStderr = "$compilerStderr\n$runtimeStderr".trim()
    val sourceLines = sourceCode.lines()

    // 1. Check GCC / Clang errors (C, C++)
    for (match in gccRegex.findAll(fullStderr)) {
      val file = match.groupValues[1]
      val line = match.groupValues[2].toIntOrNull()
      val col = match.groupValues[3].toIntOrNull()
      val type = match.groupValues[4]
      val msg = match.groupValues[5]

      val (category, en, hi, hinglish, cause, fix) = analyzeGccError(msg, line, sourceLines)
      val origSnippet = if (line != null && line in 1..sourceLines.size) sourceLines[line - 1] else ""

      issues.add(
        CodeIssue(
          title = if (type.contains("error")) "Compilation Error" else "Compiler Warning",
          category = category,
          severity = if (type.contains("error")) IssueSeverity.CRITICAL else IssueSeverity.WARNING,
          fileName = file,
          line = line,
          column = col,
          rawMessage = msg,
          explanationEn = en,
          explanationHi = hi,
          explanationHinglish = hinglish,
          likelyCause = cause,
          suggestedCorrection = fix,
          originalSnippet = origSnippet,
          correctedSnippet = generateQuickFix(origSnippet, msg, category)
        )
      )
    }

    // 2. Check Java errors
    if (issues.isEmpty()) {
      for (match in javaRegex.findAll(fullStderr)) {
        val file = match.groupValues[1]
        val line = match.groupValues[2].toIntOrNull()
        val type = match.groupValues[3]
        val msg = match.groupValues[4]

        val (category, en, hi, hinglish, cause, fix) = analyzeJavaError(msg)
        val origSnippet = if (line != null && line in 1..sourceLines.size) sourceLines[line - 1] else ""

        issues.add(
          CodeIssue(
            title = "Java Syntax / Compilation Issue",
            category = category,
            severity = if (type == "error") IssueSeverity.CRITICAL else IssueSeverity.WARNING,
            fileName = file,
            line = line,
            column = null,
            rawMessage = msg,
            explanationEn = en,
            explanationHi = hi,
            explanationHinglish = hinglish,
            likelyCause = cause,
            suggestedCorrection = fix,
            originalSnippet = origSnippet,
            correctedSnippet = generateQuickFix(origSnippet, msg, category)
          )
        )
      }
    }

    // 3. Check Python tracebacks
    if (issues.isEmpty()) {
      val pyMatch = pyRegex.find(fullStderr)
      if (pyMatch != null) {
        val file = pyMatch.groupValues[1]
        val line = pyMatch.groupValues[2].toIntOrNull()
        val errType = pyMatch.groupValues[4]
        val errMsg = pyMatch.groupValues[5]

        val (category, en, hi, hinglish, cause, fix) = analyzePythonError(errType, errMsg)
        val origSnippet = if (line != null && line in 1..sourceLines.size) sourceLines[line - 1] else ""

        issues.add(
          CodeIssue(
            title = "Python $errType",
            category = category,
            severity = IssueSeverity.HIGH,
            fileName = file,
            line = line,
            column = null,
            rawMessage = "$errType: $errMsg",
            explanationEn = en,
            explanationHi = hi,
            explanationHinglish = hinglish,
            likelyCause = cause,
            suggestedCorrection = fix,
            originalSnippet = origSnippet,
            correctedSnippet = generateQuickFix(origSnippet, "$errType: $errMsg", category)
          )
        )
      } else {
        // Fallback simple error match for python/runtime
        val simpleMatch = pySimpleRegex.find(fullStderr)
        if (simpleMatch != null) {
          val errType = simpleMatch.groupValues[1]
          val errMsg = simpleMatch.groupValues[2]
          val (category, en, hi, hinglish, cause, fix) = analyzePythonError(errType, errMsg)
          issues.add(
            CodeIssue(
              title = "Runtime $errType",
              category = category,
              severity = IssueSeverity.HIGH,
              fileName = fileName,
              line = null,
              column = null,
              rawMessage = "$errType: $errMsg",
              explanationEn = en,
              explanationHi = hi,
              explanationHinglish = hinglish,
              likelyCause = cause,
              suggestedCorrection = fix,
              originalSnippet = "",
              correctedSnippet = ""
            )
          )
        }
      }
    }

    // 4. Fallback if stderr is non-empty but couldn't regex match
    if (issues.isEmpty() && fullStderr.isNotBlank()) {
      val firstLine = fullStderr.lines().firstOrNull { it.isNotBlank() } ?: "Unknown Error"
      issues.add(
        CodeIssue(
          title = "Diagnostic Error Reported",
          category = "Runtime Failure",
          severity = IssueSeverity.HIGH,
          fileName = fileName,
          line = null,
          column = null,
          rawMessage = firstLine,
          explanationEn = "The program encountered an execution failure: $firstLine",
          explanationHi = "प्रोग्राम में निष्पादन त्रुटि (execution failure) आई है: $firstLine",
          explanationHinglish = "Program run karte waqt execution error aayi hai: $firstLine. Code me syntax ya logic check karein.",
          likelyCause = "Unexpected error or invalid syntax during compilation/run.",
          suggestedCorrection = "Review the latest changes around this file.",
          originalSnippet = "",
          correctedSnippet = ""
        )
      )
    }

    return issues
  }

  private fun analyzeGccError(
    msg: String,
    line: Int?,
    lines: List<String>
  ): ExplanationBundle {
    val m = msg.lowercase()
    return when {
      m.contains("expected ';'") || m.contains("expected ';' before") -> ExplanationBundle(
        category = "Missing Semicolon",
        en = "A statement semicolon ';' is missing at the end of the line.",
        hi = "इस लाइन के अंत में सेमीकोलन ';' छूट गया है। C/C++ में प्रत्येक निर्देश के बाद ';' लगाना अनिवार्य है।",
        hinglish = "Line ke end me semicolon ';' miss ho gaya hai. C/C++ me statement end karne ke liye ';' zaroori hota hai.",
        cause = "Forgotten ';' at statement termination.",
        fix = "Add ';' at the end of the statement line."
      )
      m.contains("was not declared in this scope") || m.contains("undeclared") -> ExplanationBundle(
        category = "Undefined Variable/Function",
        en = "You are using an identifier that has not been defined or declared in the current scope.",
        hi = "आप एक ऐसे वेरिएबल या फंक्शन का उपयोग कर रहे हैं जिसे घोषित (declare) नहीं किया गया है।",
        hinglish = "Aapne jo variable ya function use kiya hai wo declare nahi kiya gaya hai ya uski spelling galat hai.",
        cause = "Variable typo or missing declaration / #include header.",
        fix = "Verify the variable spelling or declare it with its type before using."
      )
      m.contains("no such file or directory") -> ExplanationBundle(
        category = "Missing Header File",
        en = "The compiler cannot locate the included header file.",
        hi = "कंपाइलर को इन्क्लूड की गई हेडर फाइल नहीं मिली। स्पेलिंग की जांच करें।",
        hinglish = "Compiler ko include ki gayi header file nahi mili. #include <filename> ki spelling check karein.",
        cause = "Typo in #include directive or unsupported library.",
        fix = "Check standard library header spelling (e.g., <iostream>, <vector>)."
      )
      else -> ExplanationBundle(
        category = "Syntax / Compiler Issue",
        en = "The compiler encountered an invalid token or expression: $msg",
        hi = "कंपाइलर को कोड में अमान्य अभिव्यक्ति मिली: $msg",
        hinglish = "Compiler ko code me problem mili: $msg. Syntax check karein.",
        cause = "Mismatched syntax or invalid type casting.",
        fix = "Inspect syntax near line $line."
      )
    }
  }

  private fun analyzePythonError(errType: String, msg: String): ExplanationBundle {
    return when (errType) {
      "IndentationError" -> ExplanationBundle(
        category = "Indentation Error",
        en = "Python relies strictly on indentation. The code block has uneven spaces or tabs.",
        hi = "पायथन में इंडेंटेशन (spaces) अनिवार्य है। इस कोड ब्लॉक में स्पेस या टैब का अंतर है।",
        hinglish = "Python me indentation ka dhyaan rakhna padta hai. Is line me spaces ya tabs mismatched hain. 4 spaces use karein.",
        cause = "Mixing tabs and spaces or missing indentation after if/for/def.",
        fix = "Indent this line with 4 spaces."
      )
      "NameError" -> ExplanationBundle(
        category = "Undefined Identifier",
        en = "The variable or function is not defined: $msg",
        hi = "यह वेरिएबल या फंक्शन परिभाषित नहीं है: $msg। पहले इसे डिफाइन करें।",
        hinglish = "Yeh variable ya function define nahi kiya gaya hai: $msg. Pehle ise assign ya define karein.",
        cause = "Variable referenced before assignment or typo in name.",
        fix = "Define the variable before reading it."
      )
      "SyntaxError" -> ExplanationBundle(
        category = "Syntax Error",
        en = "Invalid Python syntax. Often caused by missing colons ':' or unmatched parentheses.",
        hi = "अमान्य पायथन सिंटैक्स। अक्सर if/def के बाद कोलन ':' या कोष्ठक न बंद करने से होता है।",
        hinglish = "Python syntax galat hai. Kya aap if/for/def ke baad colon ':' lagana bhool gaye ya bracket close nahi kiya?",
        cause = "Missing colon ':' after compound statements or unmatched brackets.",
        fix = "Check for ':' at the end of function/loop header."
      )
      "TypeError" -> ExplanationBundle(
        category = "Type Mismatch",
        en = "An operation was attempted on incompatible data types ($msg).",
        hi = "असंगत डेटा प्रकारों पर कार्य करने का प्रयास किया गया ($msg)।",
        hinglish = "Do alag types (jaise string aur int) ke beech operation karne ki koshish ki gayi ($msg).",
        cause = "Concatenating string with integer without str() conversion.",
        fix = "Cast variables using str() or int()."
      )
      "ZeroDivisionError" -> ExplanationBundle(
        category = "Math Error",
        en = "Division or modulo by zero is not mathematically allowed.",
        hi = "शून्य (0) से भाग देना संभव नहीं है।",
        hinglish = "Kisi number ko 0 se divide nahi kiya ja sakta. Divisor ko zero hone se bachayein.",
        cause = "Divisor evaluated to 0.",
        fix = "Add a condition `if divisor != 0:` before division."
      )
      else -> ExplanationBundle(
        category = "Runtime Exception",
        en = "Runtime error occurred: $msg",
        hi = "प्रोग्राम चलते समय त्रुटि आई: $msg",
        hinglish = "Program run hote waqt exception aayi: $msg.",
        cause = "Unchecked exception during script execution.",
        fix = "Handle edge cases or wrap in try/except block."
      )
    }
  }

  private fun analyzeJavaError(msg: String): ExplanationBundle {
    val m = msg.lowercase()
    return when {
      m.contains("cannot find symbol") -> ExplanationBundle(
        category = "Undefined Symbol",
        en = "Java cannot find the specified variable, method, or class.",
        hi = "जावा इस वेरिएबल, मेथड या क्लास को नहीं ढूंढ पा रहा है।",
        hinglish = "Java ko yeh symbol nahi mil raha. Check karein kya import ya declaration miss hui hai.",
        cause = "Missing import or undeclared variable.",
        fix = "Declare the variable or add the required import statement."
      )
      m.contains("';' expected") -> ExplanationBundle(
        category = "Missing Semicolon",
        en = "A semicolon ';' is required at the end of this Java statement.",
        hi = "जावा में इस स्टेटमेंट के बाद सेमीकोलन ';' लगाना आवश्यक है।",
        hinglish = "Is Java statement ke end me semicolon ';' miss ho gaya hai.",
        cause = "Missing semicolon termination.",
        fix = "Append ';' to the line."
      )
      else -> ExplanationBundle(
        category = "Java Compilation Error",
        en = "Java compiler error: $msg",
        hi = "जावा कंपाइलर त्रुटि: $msg",
        hinglish = "Java compiler issue: $msg. Check types and syntax.",
        cause = "Type mismatch or invalid Java syntax.",
        fix = "Review Java method signatures and variable types."
      )
    }
  }

  private fun generateQuickFix(origLine: String, msg: String, category: String): String {
    if (origLine.isBlank()) return ""
    return when {
      category == "Missing Semicolon" && !origLine.trimEnd().endsWith(";") -> {
        origLine.trimEnd() + ";"
      }
      category == "Indentation Error" -> {
        "    " + origLine.trimStart()
      }
      category == "Syntax Error" && (origLine.contains("def ") || origLine.contains("if ") || origLine.contains("for ")) && !origLine.trimEnd().endsWith(":") -> {
        origLine.trimEnd() + ":"
      }
      else -> origLine
    }
  }

  data class ExplanationBundle(
    val category: String,
    val en: String,
    val hi: String,
    val hinglish: String,
    val cause: String,
    val fix: String
  )
}
