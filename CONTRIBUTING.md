# Contributing to JManhunt
Thank you for your interest in contributing to JManhunt. To maintain a high quality of code and a stable API, please follow these guidelines.

## General Rules
* Be respectful to other community members.
* Use our Discord for general questions or discussions.
* For major changes, please open an issue first to discuss your proposal.

## Setup
* Java JDK 21 or higher is required.
* Build system: Maven 3.8+.
* IDE: IntelliJ IDEA is recommended.

To build the project, run:
`mvn clean package`

## Pull Request Guidelines
+ Changes to interfaces and public methods must stay in the api module.
+ Implementation logic belongs in the plugin module.
+ Keep the existing code style and use clear naming for variables and methods.
+ Write concise commit messages (e.g., fix: resolve team data leak).
+ Use [this](https://github.com/yblacky/yblacky/blob/main/convential-commits.md) commit message style
+ Avoid breaking changes in the API whenever possible.

## Bug Reports
Please provide a clear description of the bug, steps to reproduce it, and your server logs or stacktraces.

Thank you for helping us improve the engine.<br>
The **Frostleaf Team**