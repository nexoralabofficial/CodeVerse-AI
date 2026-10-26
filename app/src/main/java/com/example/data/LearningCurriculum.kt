package com.example.data

import com.example.model.LanguageId

data class LearningLesson(
  val id: String,
  val title: String,
  val category: String,
  val difficulty: String,
  val languageId: LanguageId,
  val summary: String,
  val explanation: String,
  val starterCode: String,
  val keyTakeaways: List<String>
)

object LearningCurriculum {
  val lessons: List<LearningLesson> = listOf(
    LearningLesson(
      id = "lesson_hello_world",
      title = "Hello World & Program Structure",
      category = "Basics",
      difficulty = "Beginner",
      languageId = LanguageId.PYTHON,
      summary = "Understand how code executes sequentially and prints output to the console.",
      explanation = "Every programming journey begins with outputting a message. In Python, the `print()` function sends text to the standard output console. Notice that text must be enclosed within quotation marks (strings).",
      starterCode = """# Lesson 1: Hello World in Python
# print() writes to standard output

message = "Hello, World! Welcome to CodeVerse AI."
print(message)

# You can print calculations directly:
print("2 + 2 =", 2 + 2)
""",
      keyTakeaways = listOf(
        "Code runs line by line from top to bottom.",
        "Strings are enclosed in double or single quotes.",
        "print() writes output directly to the IDE console."
      )
    ),
    LearningLesson(
      id = "lesson_variables",
      title = "Variables & Dynamic Types",
      category = "Basics",
      difficulty = "Beginner",
      languageId = LanguageId.PYTHON,
      summary = "Store numbers, text, and booleans in reusable memory variables.",
      explanation = "Variables act as labeled containers for data in memory. In Python, variables are dynamically typed, meaning you don't need to explicitly declare their type. Python infers whether it is an integer, float, string, or boolean.",
      starterCode = """# Variables and Types
user_name = "Alex"
user_age = 22
gpa = 3.85
is_enrolled = True

print(f"Student: {user_name}, Age: {user_age}")
print(f"GPA: {gpa}, Active: {is_enrolled}")
print(f"Type of user_name: {type(user_name)}")
print(f"Type of gpa: {type(gpa)}")
""",
      keyTakeaways = listOf(
        "Use descriptive snake_case names for variables.",
        "f-strings (f'...') allow easy variable embedding.",
        "Use type() to inspect data types."
      )
    ),
    LearningLesson(
      id = "lesson_conditions",
      title = "Conditions & Decision Making",
      category = "Control Flow",
      difficulty = "Beginner",
      languageId = LanguageId.PYTHON,
      summary = "Branch your code using if, elif, and else statements.",
      explanation = "Conditional statements let your program make smart choices based on boolean logic (True or False). Python uses indentation to indicate blocks of code that execute only when the condition is met.",
      starterCode = """# Decision Making
score = 85

if score >= 90:
    grade = "A (Excellent)"
elif score >= 80:
    grade = "B (Good Job)"
elif score >= 70:
    grade = "C (Pass)"
else:
    grade = "Needs Improvement"

print(f"Score: {score} -> Grade: {grade}")
""",
      keyTakeaways = listOf(
        "Always end 'if', 'elif', and 'else' lines with a colon (:).",
        "Indent the body of the if block with 4 spaces.",
        "Comparison operators: ==, !=, >, <, >=, <="
      )
    ),
    LearningLesson(
      id = "lesson_loops",
      title = "Loops & Iterations",
      category = "Control Flow",
      difficulty = "Beginner",
      languageId = LanguageId.CPP,
      summary = "Repeat tasks automatically using for and while loops in C++.",
      explanation = "Loops let you repeat code efficiently without copy-pasting. In C++, a standard for loop consists of initialization, condition, and increment. While loops repeat as long as a condition holds true.",
      starterCode = """// C++ Loops Demonstration
#include <iostream>

int main() {
    std::cout << "--- For Loop Counting ---" << std::endl;
    for (int i = 1; i <= 5; ++i) {
        std::cout << "Count: " << i << std::endl;
    }

    std::cout << "\n--- While Loop Countdown ---" << std::endl;
    int timer = 3;
    while (timer > 0) {
        std::cout << "T-minus: " << timer << std::endl;
        timer--;
    }
    std::cout << "Liftoff!" << std::endl;
    return 0;
}
""",
      keyTakeaways = listOf(
        "for loops are best when iteration count is known.",
        "while loops are best when waiting for a condition.",
        "Always ensure your loop has an exit condition to avoid infinite loops."
      )
    ),
    LearningLesson(
      id = "lesson_functions",
      title = "Functions & Modular Code",
      category = "Functions",
      difficulty = "Intermediate",
      languageId = LanguageId.PYTHON,
      summary = "Write clean, reusable functions with parameters and return values.",
      explanation = "Functions group logic into self-contained modules that can be called repeatedly with different inputs. The `def` keyword defines a function, and `return` sends data back to the caller.",
      starterCode = """# Functions in Python
def calculate_area(length: float, width: float) -> float:
    '''Calculates area of a rectangle.'''
    return length * width

def greet_developer(name: String = "Coder"):
    print(f"Hello, {name}! Ready to build with CodeVerse AI?")

greet_developer("Sarah")
area = calculate_area(12.5, 8.0)
print(f"Calculated Area: {area} sq units")
""",
      keyTakeaways = listOf(
        "Functions follow the DRY principle: Don't Repeat Yourself.",
        "Type hints (e.g. length: float) make code readable and safe.",
        "Functions can take default argument values."
      )
    ),
    LearningLesson(
      id = "lesson_arrays",
      title = "Arrays & Data Collections",
      category = "Data Structures",
      difficulty = "Intermediate",
      languageId = LanguageId.JAVASCRIPT,
      summary = "Manipulate lists and datasets with map, filter, and reduce.",
      explanation = "Arrays store multiple values in a single ordered container. Modern JavaScript provides powerful functional methods like .map(), .filter(), and .reduce() to transform collections with minimal code.",
      starterCode = """// JavaScript Array Transformations
const products = [
  { name: "Mechanical Keyboard", price: 95 },
  { name: "Gaming Mouse", price: 45 },
  { name: "4K Monitor", price: 320 },
  { name: "USB-C Cable", price: 15 }
];

// 1. Filter items under $100
const budgetItems = products.filter(item => item.price < 100);
console.log("Budget Items (<$100):", budgetItems.map(i => i.name));

// 2. Calculate total cart value
const totalPrice = products.reduce((sum, item) => sum + item.price, 0);
console.log("Total Cart Value: $" + totalPrice);
""",
      keyTakeaways = listOf(
        "Arrays are 0-indexed.",
        ".filter() extracts matching items.",
        ".reduce() aggregates collections into a single result."
      )
    ),
    LearningLesson(
      id = "lesson_oop",
      title = "Object-Oriented Programming (OOP)",
      category = "Architecture",
      difficulty = "Intermediate",
      languageId = LanguageId.JAVA,
      summary = "Model real-world entities with classes, attributes, and methods in Java.",
      explanation = "OOP organizes software around objects rather than functions. A class serves as a blueprint, containing fields (state) and methods (behavior). Encapsulation protects private state through getters and setters.",
      starterCode = """// Java Object-Oriented Blueprint
class BankAccount {
    private String owner;
    private double balance;

    public BankAccount(String owner, double initialDeposit) {
        this.owner = owner;
        this.balance = initialDeposit;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Deposited: $" + amount + " | Balance: $" + balance);
        }
    }

    public void displaySummary() {
        System.out.println("Account Owner: " + owner + " | Balance: $" + balance);
    }
}

public class Main {
    public static void main(String[] args) {
        BankAccount account = new BankAccount("Alice", 250.0);
        account.displaySummary();
        account.deposit(150.0);
    }
}
""",
      keyTakeaways = listOf(
        "Classes are blueprints; instances are objects in memory.",
        "Use private fields to protect data integrity.",
        "Constructors initialize object state upon creation."
      )
    ),
    LearningLesson(
      id = "lesson_dsa",
      title = "Data Structures: Stack & LIFO",
      category = "Algorithms",
      difficulty = "Advanced",
      languageId = LanguageId.CPP,
      summary = "Implement a Last-In First-Out (LIFO) stack data structure.",
      explanation = "A Stack is a fundamental data structure following the LIFO principle: elements are pushed onto the top and popped off the top. Used everywhere in compilers, undo engines, and function call stacks.",
      starterCode = """// C++ Stack Implementation
#include <iostream>
#include <vector>

class SimpleStack {
private:
    std::vector<int> elements;
public:
    void push(int value) {
        elements.push_back(value);
        std::cout << "Pushed: " << value << std::endl;
    }

    int pop() {
        if (elements.empty()) {
            std::cout << "Stack Underflow!" << std::endl;
            return -1;
        }
        int top = elements.back();
        elements.pop_back();
        return top;
    }

    bool isEmpty() const {
        return elements.empty();
    }
};

int main() {
    SimpleStack stack;
    stack.push(10);
    stack.push(20);
    stack.push(30);

    std::cout << "Popped: " << stack.pop() << std::endl;
    std::cout << "Popped: " << stack.pop() << std::endl;
    return 0;
}
""",
      keyTakeaways = listOf(
        "LIFO: Last-In First-Out.",
        "Push and Pop operations have O(1) time complexity.",
        "Core foundation for undo/redo and expression evaluation."
      )
    ),
    LearningLesson(
      id = "lesson_html_css",
      title = "Building a Responsive Neon Card",
      category = "Web Development",
      difficulty = "Beginner",
      languageId = LanguageId.HTML,
      summary = "Create modern glassmorphism and neon glowing cards with HTML5 and CSS3.",
      explanation = "Modern web development combines HTML for document structure and CSS for visual styling, responsive layouts, and fluid animations. You can view the output live in the in-app Web Preview!",
      starterCode = """<!DOCTYPE html>
<html>
<head>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <style>
    body {
      background: #0B0D12;
      color: #F3F4F6;
      font-family: sans-serif;
      display: flex;
      justify-content: center;
      align-items: center;
      height: 90vh;
      margin: 0;
    }
    .neon-badge {
      background: #171D29;
      border: 2px solid #6366F1;
      border-radius: 20px;
      padding: 30px;
      text-align: center;
      box-shadow: 0 0 20px rgba(99, 102, 241, 0.4);
    }
    h2 { color: #22D3EE; margin: 0 0 10px; }
    p { color: #9CA3AF; margin: 0; }
  </style>
</head>
<body>
  <div class="neon-badge">
    <h2>⚡ CodeVerse AI</h2>
    <p>Mobile IDE & AI Coding Assistant</p>
  </div>
</body>
</html>
""",
      keyTakeaways = listOf(
        "Always include viewport meta tag for mobile responsiveness.",
        "CSS Flexbox creates centered and aligned layouts easily.",
        "Box-shadow creates glowing neon effects."
      )
    ),
    LearningLesson(
      id = "lesson_js_interactive",
      title = "Interactive JavaScript Calculator",
      category = "Web Development",
      difficulty = "Intermediate",
      languageId = LanguageId.HTML,
      summary = "Handle user click events and update the DOM dynamically.",
      explanation = "JavaScript adds life to web pages by reacting to events like button taps, keystrokes, and touch gestures. Here we manipulate DOM elements directly in the live in-app preview.",
      starterCode = """<!DOCTYPE html>
<html>
<head>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <style>
    body { background: #0B0D12; color: #FFF; font-family: sans-serif; text-align: center; padding: 20px; }
    .screen { font-size: 32px; color: #22C55E; margin: 20px; font-weight: bold; }
    .btn-row button {
      padding: 12px 20px;
      margin: 6px;
      font-size: 18px;
      background: #1F2737;
      color: #F3F4F6;
      border: 1px solid #6366F1;
      border-radius: 8px;
    }
  </style>
</head>
<body>
  <h3>Micro Counter</h3>
  <div id="val" class="screen">0</div>
  <div class="btn-row">
    <button onclick="change(-1)">-1</button>
    <button onclick="change(1)">+1</button>
    <button onclick="resetVal()">Reset</button>
  </div>
  <script>
    let num = 0;
    function change(delta) {
      num += delta;
      document.getElementById('val').innerText = num;
      console.log('New value:', num);
    }
    function resetVal() {
      num = 0;
      document.getElementById('val').innerText = num;
      console.log('Counter reset');
    }
  </script>
</body>
</html>
""",
      keyTakeaways = listOf(
        "onclick attributes attach event handlers to buttons.",
        "document.getElementById() accesses DOM elements.",
        "console.log() sends debug output to the CodeVerse console."
      )
    )
  )
}
