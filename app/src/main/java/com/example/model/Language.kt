package com.example.model

enum class LanguageId {
  C,
  CPP,
  PYTHON,
  JAVA,
  JAVASCRIPT,
  TYPESCRIPT,
  HTML,
  CSS,
  SQL,
  PHP,
  CSHARP,
  KOTLIN,
  DART,
  GO,
  RUST,
  RUBY,
  SWIFT,
  BASH,
  JSON,
  XML,
  MARKDOWN
}

data class Language(
  val id: LanguageId,
  val name: String,
  val extension: String,
  val pistonLanguage: String?, // Language identifier for Piston Execution API
  val pistonVersion: String?,
  val editingSupport: Boolean = true,
  val syntaxHighlightSupport: Boolean = true,
  val staticAnalysisSupport: Boolean = true,
  val compilationSupport: Boolean,
  val executionSupport: Boolean,
  val offlineAvailability: Boolean,
  val isWebPreviewable: Boolean = false,
  val category: String,
  val iconEmoji: String,
  val defaultTemplate: String
)

object LanguageRegistry {
  val languages: List<Language> = listOf(
    Language(
      id = LanguageId.PYTHON,
      name = "Python",
      extension = "py",
      pistonLanguage = "python",
      pistonVersion = "3.10.0",
      compilationSupport = false,
      executionSupport = true,
      offlineAvailability = false,
      category = "Backend / Scripting",
      iconEmoji = "🐍",
      defaultTemplate = """# CodeVerse AI — Python 3 Script
import math
import sys

def main():
    print("Welcome to CodeVerse AI!")
    print(f"Python Version: {sys.version.split()[0]}")
    
    # Calculate Fibonacci series
    n = 10
    fib = [0, 1]
    for i in range(2, n):
        fib.append(fib[-1] + fib[-2])
    
    print(f"Fibonacci({n}): {fib}")
    print("Code. Run. Debug. Learn.")

if __name__ == "__main__":
    main()
""".trimIndent()
    ),
    Language(
      id = LanguageId.CPP,
      name = "C++",
      extension = "cpp",
      pistonLanguage = "c++",
      pistonVersion = "10.2.0",
      compilationSupport = true,
      executionSupport = true,
      offlineAvailability = false,
      category = "Systems / High Performance",
      iconEmoji = "⚡",
      defaultTemplate = """// CodeVerse AI — C++20 Standard
#include <iostream>
#include <vector>
#include <numeric>

int main() {
    std::cout << "=== CodeVerse AI C++ Environment ===" << std::endl;
    std::vector<int> numbers = {10, 20, 30, 40, 50};
    
    long long sum = 0;
    for (int num : numbers) {
        sum += num;
    }
    
    std::cout << "Sum of elements: " << sum << std::endl;
    std::cout << "Execution Successful!" << std::endl;
    return 0;
}
""".trimIndent()
    ),
    Language(
      id = LanguageId.C,
      name = "C",
      extension = "c",
      pistonLanguage = "c",
      pistonVersion = "10.2.0",
      compilationSupport = true,
      executionSupport = true,
      offlineAvailability = false,
      category = "Systems Programming",
      iconEmoji = "🔧",
      defaultTemplate = """/* CodeVerse AI — Standard C99/C11 */
#include <stdio.h>

int main() {
    printf("Welcome to CodeVerse AI — C Programming!\n");
    int a = 15;
    int b = 27;
    int sum = a + b;
    printf("Result of %d + %d = %d\n", a, b, sum);
    return 0;
}
""".trimIndent()
    ),
    Language(
      id = LanguageId.HTML,
      name = "HTML",
      extension = "html",
      pistonLanguage = null,
      pistonVersion = null,
      compilationSupport = false,
      executionSupport = true,
      offlineAvailability = true,
      isWebPreviewable = true,
      category = "Web Development",
      iconEmoji = "🌐",
      defaultTemplate = """<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>CodeVerse AI Web Canvas</title>
  <style>
    body {
      background: #0B0D12;
      color: #F3F4F6;
      font-family: system-ui, -apple-system, sans-serif;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      min-height: 90vh;
      margin: 0;
      padding: 16px;
      box-sizing: border-box;
      text-align: center;
    }
    .card {
      background: #171D29;
      border: 1px solid #6366F1;
      border-radius: 16px;
      padding: 24px;
      max-width: 400px;
      box-shadow: 0 10px 25px rgba(99, 102, 241, 0.2);
    }
    h1 {
      color: #22D3EE;
      margin-top: 0;
      font-size: 24px;
    }
    p {
      color: #9CA3AF;
      font-size: 14px;
      line-height: 1.5;
    }
    button {
      background: linear-gradient(135deg, #6366F1, #8B5CF6);
      color: #FFFFFF;
      border: none;
      padding: 12px 24px;
      border-radius: 8px;
      font-weight: bold;
      cursor: pointer;
      margin-top: 12px;
      font-size: 15px;
    }
    button:active {
      transform: scale(0.98);
    }
    #counter-val {
      color: #22C55E;
      font-size: 28px;
      font-weight: bold;
      margin: 12px 0;
    }
  </style>
</head>
<body>
  <div class="card">
    <h1>CodeVerse AI</h1>
    <p>Live In-App Web Preview with real-time DOM rendering and console logging.</p>
    <div id="counter-val">0</div>
    <button onclick="incrementCounter()">Click Me!</button>
  </div>

  <script>
    console.log("CodeVerse Web Preview initialized!");
    let count = 0;
    function incrementCounter() {
      count++;
      document.getElementById('counter-val').innerText = count;
      console.log("Button clicked, count is now:", count);
    }
  </script>
</body>
</html>
""".trimIndent()
    ),
    Language(
      id = LanguageId.JAVASCRIPT,
      name = "JavaScript",
      extension = "js",
      pistonLanguage = "javascript",
      pistonVersion = "18.15.0",
      compilationSupport = false,
      executionSupport = true,
      offlineAvailability = false,
      isWebPreviewable = true,
      category = "Web / Runtime",
      iconEmoji = "💛",
      defaultTemplate = """// CodeVerse AI — Node.js / JavaScript
console.log("=== CodeVerse AI JavaScript Runner ===");

const users = [
  { id: 1, name: "Alice", role: "Dev" },
  { id: 2, name: "Bob", role: "Designer" },
  { id: 3, name: "Charlie", role: "Architect" }
];

const teamSummary = users.map(u => u.name + " (" + u.role + ")").join(", ");
console.log("Team:", teamSummary);
console.log("Timestamp:", new Date().toISOString());
""".trimIndent()
    ),
    Language(
      id = LanguageId.JAVA,
      name = "Java",
      extension = "java",
      pistonLanguage = "java",
      pistonVersion = "15.0.2",
      compilationSupport = true,
      executionSupport = true,
      offlineAvailability = false,
      category = "Enterprise / Android",
      iconEmoji = "☕",
      defaultTemplate = """// CodeVerse AI — Java 15
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("CodeVerse AI — Java Execution");
        
        List<String> modules = new ArrayList<>();
        modules.add("Editor");
        modules.add("CodeSense AI");
        modules.add("Console");
        modules.add("Debugger");
        
        System.out.println("Loaded Modules:");
        for (int i = 0; i < modules.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, modules.get(i));
        }
    }
}
""".trimIndent()
    ),
    Language(
      id = LanguageId.RUST,
      name = "Rust",
      extension = "rs",
      pistonLanguage = "rust",
      pistonVersion = "1.68.2",
      compilationSupport = true,
      executionSupport = true,
      offlineAvailability = false,
      category = "Systems & Memory Safety",
      iconEmoji = "🦀",
      defaultTemplate = """// CodeVerse AI — Rust 2021 Edition
fn main() {
    println!("Hello from CodeVerse AI Rust!");
    let mut numbers = vec![5, 2, 8, 1, 9];
    numbers.sort();
    println!("Sorted array: {:?}", numbers);
    
    let sum: i32 = numbers.iter().sum();
    println!("Total sum: {}", sum);
}
""".trimIndent()
    ),
    Language(
      id = LanguageId.GO,
      name = "Go",
      extension = "go",
      pistonLanguage = "go",
      pistonVersion = "1.16.2",
      compilationSupport = true,
      executionSupport = true,
      offlineAvailability = false,
      category = "Cloud & Concurrency",
      iconEmoji = "🐹",
      defaultTemplate = """package main

import (
	"fmt"
	"time"
)

func main() {
	fmt.Println("CodeVerse AI — Go Concurrent Runner")
	ch := make(chan string)

	go func() {
		time.Sleep(100 * time.Millisecond)
		ch <- "Goroutine task completed!"
	}()

	msg := <-ch
	fmt.Println("Received:", msg)
}
""".trimIndent()
    ),
    Language(
      id = LanguageId.KOTLIN,
      name = "Kotlin",
      extension = "kt",
      pistonLanguage = "kotlin",
      pistonVersion = "1.8.20",
      compilationSupport = true,
      executionSupport = true,
      offlineAvailability = false,
      category = "Modern Android & JVM",
      iconEmoji = "🟣",
      defaultTemplate = """// CodeVerse AI — Kotlin Runner
data class Task(val id: Int, val title: String, val completed: Boolean)

fun main() {
    val tasks = listOf(
        Task(1, "Create project", true),
        Task(2, "Write clean code", true),
        Task(3, "Deploy with CodeVerse", false)
    )
    
    println("CodeVerse AI Kotlin:")
    tasks.filter { !it.completed }.forEach {
        println("Pending: ${'$'}{it.title}")
    }
}
""".trimIndent()
    ),
    Language(
      id = LanguageId.TYPESCRIPT,
      name = "TypeScript",
      extension = "ts",
      pistonLanguage = "typescript",
      pistonVersion = "5.0.3",
      compilationSupport = true,
      executionSupport = true,
      offlineAvailability = false,
      category = "Typed Web",
      iconEmoji = "🔷",
      defaultTemplate = """// CodeVerse AI — TypeScript
interface CodeStats {
  files: number;
  lines: number;
  language: string;
}

const stats: CodeStats = {
  files: 12,
  lines: 840,
  language: "TypeScript"
};

console.log("CodeVerse Project: " + stats.language);
console.log("Total files: " + stats.files + ", Total lines: " + stats.lines);
""".trimIndent()
    ),
    Language(
      id = LanguageId.BASH,
      name = "Bash",
      extension = "sh",
      pistonLanguage = "bash",
      pistonVersion = "5.2.0",
      compilationSupport = false,
      executionSupport = true,
      offlineAvailability = false,
      category = "Scripting & Shell",
      iconEmoji = "💻",
      defaultTemplate = """#!/usr/bin/env bash
# CodeVerse AI — Bash Shell Script
echo "=== Shell System Info ==="
echo "Date: $(date)"
echo "Current directory: $(pwd)"
echo "Arguments count: $#"
echo "Done!"
""".trimIndent()
    ),
    Language(
      id = LanguageId.SQL,
      name = "SQL",
      extension = "sql",
      pistonLanguage = "sqlite3",
      pistonVersion = "3.36.0",
      compilationSupport = false,
      executionSupport = true,
      offlineAvailability = false,
      category = "Databases",
      iconEmoji = "🗄️",
      defaultTemplate = """-- CodeVerse AI — SQLite3 Script
CREATE TABLE developers (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    favorite_lang TEXT NOT NULL,
    years_exp INTEGER
);

INSERT INTO developers (name, favorite_lang, years_exp) VALUES
('Alex', 'Kotlin', 5),
('Sam', 'Rust', 4),
('Maya', 'Python', 6);

SELECT * FROM developers WHERE years_exp >= 5;
""".trimIndent()
    ),
    Language(
      id = LanguageId.PHP,
      name = "PHP",
      extension = "php",
      pistonLanguage = "php",
      pistonVersion = "8.2.3",
      compilationSupport = false,
      executionSupport = true,
      offlineAvailability = false,
      category = "Web Backend",
      iconEmoji = "🐘",
      defaultTemplate = """<?php
// CodeVerse AI — PHP 8.2
echo "CodeVerse AI PHP Runner\n";
${'$'}items = ["Editor", "AI Debugger", "Compiler", "Console"];
foreach (${'$'}items as ${'$'}index => ${'$'}item) {
    echo (${'$'}index + 1) . ". " . ${'$'}item . "\n";
}
""".trimIndent()
    ),
    Language(
      id = LanguageId.CSHARP,
      name = "C#",
      extension = "cs",
      pistonLanguage = "csharp.net",
      pistonVersion = "7.0.400",
      compilationSupport = true,
      executionSupport = true,
      offlineAvailability = false,
      category = ".NET / Gaming",
      iconEmoji = "🎯",
      defaultTemplate = """using System;

class Program {
    static void Main() {
        Console.WriteLine("CodeVerse AI — C# Program");
        for (int i = 1; i <= 5; i++) {
            Console.WriteLine("Step " + i + ": Ready to code.");
        }
    }
}
""".trimIndent()
    ),
    Language(
      id = LanguageId.RUBY,
      name = "Ruby",
      extension = "rb",
      pistonLanguage = "ruby",
      pistonVersion = "3.0.1",
      compilationSupport = false,
      executionSupport = true,
      offlineAvailability = false,
      category = "Scripting",
      iconEmoji = "💎",
      defaultTemplate = """# CodeVerse AI — Ruby
puts "Hello from CodeVerse AI Ruby!"
languages = ["Ruby", "Python", "Kotlin"]
languages.each_with_index do |lang, idx|
  puts "#{idx + 1}. #{lang}"
end
""".trimIndent()
    ),
    Language(
      id = LanguageId.SWIFT,
      name = "Swift",
      extension = "swift",
      pistonLanguage = "swift",
      pistonVersion = "5.3.3",
      compilationSupport = true,
      executionSupport = true,
      offlineAvailability = false,
      category = "Apple Ecosystem",
      iconEmoji = "🕊️",
      defaultTemplate = """// CodeVerse AI — Swift
import Foundation

let greeting = "Welcome to CodeVerse AI Swift"
print(greeting)
let numbers = [1, 2, 3, 4, 5]
let doubled = numbers.map { ${'$'}0 * 2 }
print("Doubled: \(doubled)")
""".trimIndent()
    ),
    Language(
      id = LanguageId.DART,
      name = "Dart",
      extension = "dart",
      pistonLanguage = "dart",
      pistonVersion = "2.19.6",
      compilationSupport = true,
      executionSupport = true,
      offlineAvailability = false,
      category = "Cross-Platform",
      iconEmoji = "🎯",
      defaultTemplate = """// CodeVerse AI — Dart
void main() {
  print('Hello from CodeVerse AI Dart!');
  var frameworks = ['Flutter', 'Compose', 'React'];
  frameworks.forEach(print);
}
""".trimIndent()
    ),
    Language(
      id = LanguageId.CSS,
      name = "CSS",
      extension = "css",
      pistonLanguage = null,
      pistonVersion = null,
      compilationSupport = false,
      executionSupport = false,
      offlineAvailability = true,
      isWebPreviewable = true,
      category = "Web Styling",
      iconEmoji = "🎨",
      defaultTemplate = """/* CodeVerse AI — Stylesheet */
:root {
  --primary-color: #6366F1;
  --bg-color: #0B0D12;
  --text-color: #F3F4F6;
}

body {
  margin: 0;
  padding: 20px;
  background-color: var(--bg-color);
  color: var(--text-color);
  font-family: 'Inter', sans-serif;
}
""".trimIndent()
    ),
    Language(
      id = LanguageId.JSON,
      name = "JSON",
      extension = "json",
      pistonLanguage = null,
      pistonVersion = null,
      compilationSupport = false,
      executionSupport = false,
      offlineAvailability = true,
      category = "Data Format",
      iconEmoji = "📋",
      defaultTemplate = """{
  "appName": "CodeVerse AI",
  "version": "1.0.0",
  "features": [
    "Syntax Highlighting",
    "Real Execution Engine",
    "CodeSense AI Debugger",
    "In-App Web Preview"
  ],
  "active": true
}
""".trimIndent()
    ),
    Language(
      id = LanguageId.XML,
      name = "XML",
      extension = "xml",
      pistonLanguage = null,
      pistonVersion = null,
      compilationSupport = false,
      executionSupport = false,
      offlineAvailability = true,
      category = "Markup Format",
      iconEmoji = "📄",
      defaultTemplate = """<?xml version="1.0" encoding="UTF-8"?>
<codeverse>
    <project name="DemoApp">
        <language>Kotlin</language>
        <status>Active</status>
    </project>
</codeverse>
""".trimIndent()
    ),
    Language(
      id = LanguageId.MARKDOWN,
      name = "Markdown",
      extension = "md",
      pistonLanguage = null,
      pistonVersion = null,
      compilationSupport = false,
      executionSupport = false,
      offlineAvailability = true,
      category = "Documentation",
      iconEmoji = "📝",
      defaultTemplate = """# CodeVerse AI
> Code. Run. Debug. Learn.

## Features
- **Multi-Language IDE**: Edit 20+ languages with syntax highlighting.
- **Real Execution**: Real compiler diagnostics for C, C++, Python, Java, Rust & more.
- **CodeSense AI**: Automated bug explanation and one-click patch application.
""".trimIndent()
    )
  )

  fun getById(id: LanguageId): Language =
    languages.firstOrNull { it.id == id } ?: languages.first()

  fun getByExtension(ext: String): Language =
    languages.firstOrNull { it.extension.equals(ext, ignoreCase = true) }
      ?: languages.first { it.id == LanguageId.PYTHON }
}
