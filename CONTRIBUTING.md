# Contributing to hateoflux

Thank you for your interest in contributing to hateoflux! We welcome and appreciate all forms of contributions, from reporting bugs and suggesting features to submitting code changes and improving documentation.

This guide will help you understand how to contribute effectively to hateoflux.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How Can I Contribute?](#how-can-i-contribute)
  - [Reporting Bugs](#reporting-bugs)
  - [Suggesting Enhancements](#suggesting-enhancements)
  - [Pull Requests](#pull-requests)
    - [Setup Your Development Environment](#setup-your-development-environment)
    - [Fork the Repository](#fork-the-repository)
    - [Create a Feature Branch](#create-a-feature-branch)
    - [Commit Your Changes](#commit-your-changes)
    - [Push to Your Fork](#push-to-your-fork)
    - [Open a Pull Request](#open-a-pull-request)
- [Coding Guidelines](#coding-guidelines)
  - [Style Guide](#style-guide)
  - [Documentation](#documentation)
  - [Testing](#testing)
- [Issue Guidelines](#issue-guidelines)
- [License](#license)
- [Contact](#contact)

## Code of Conduct

Please read and follow our [Code of Conduct](CODE_OF_CONDUCT.md) to ensure a welcoming and respectful environment for all contributors.

## How Can I Contribute?

There are several ways you can contribute to hateoflux:

### Reporting Bugs

If you find a bug in hateoflux, please help us fix it by following these steps:

1. **Check Existing Issues:** Before creating a new issue, search the [existing issues](https://github.com/kamillionlabs/hateoflux/issues) to see if the problem has already been reported.
2. **Open a New Issue:** If you don't find an existing issue, open a new one by providing:
   - A clear and descriptive title.
   - A detailed description of the problem.
   - Steps to reproduce the issue.
   - Expected and actual behavior.
   - Environment details (e.g., Java version, Spring version, etc.).
   - Any relevant logs or error messages.

### Suggesting Enhancements

We love hearing new ideas! To suggest a new feature or enhancement:

1. **Search Existing Issues:** Ensure that your idea hasn't already been proposed by searching the [issue tracker](https://github.com/kamillionlabs/hateoflux/issues).
2. **Open a Feature Request:** Create a new issue with:
   - A clear and descriptive title.
   - A detailed explanation of the enhancement.
   - The problem it solves or the value it adds.
   - Any relevant examples or use cases.

### Pull Requests

Contributing code is a great way to help improve hateoflux. Follow these steps to submit a pull request:

#### Setup Your Development Environment

1. **Clone the Repository:**

```bash
# HTTPS
git clone https://github.com/kamillionlabs/hateoflux.git

# SSH
git clone git@github.com:kamillionlabs/hateoflux.git

cd hateoflux
```
1. **Ensure You Have Adequate Dev-Tools:** 
   1. Ensure that Java 17 or later is installed. 
   2. While hateoflux is primarily developed using IntelliJ, you are free to use any IDE of your choice. However, in order to not mess up formatting, please import the code style from [eclipse_codestyle.xml](/code_style/eclipse_codestyle.xml). The XML can be imported at least both in eclipse and in intelliJ.


2. **Build the Project:**
```bash
./gradlew build
```
3. Run Tests:
```bash
./gradlew test
```
#### Fork the Repository
Fork the hateoflux repository to your own GitHub account by clicking the Fork button at the top right of the repository page.

#### Create a Feature Branch
Create a new branch for your feature or bugfix:
```bash
git checkout -b your-feature-name
```

#### Commit Your Changes
Make your changes and commit them with clear and descriptive messages:
```bash
git add .
git commit -m "Add feature: Description of your feature"
```

#### Push to Your Fork
Push your changes to your forked repository:
```bash
git push origin your-feature-name
```

#### Open a Pull Request
1. Navigate to the original [hateoflux repository](https://github.com/kamillionlabs/hateoflux).
1. Click on the Pull Requests tab.
1. Click New Pull Request.
1. Select your fork and the branch you just pushed.
1. Provide a clear and descriptive title and description for your pull request.
1. Link any related issues by including `Closes #issue-number` in your description.
1. Submit the pull request.

> [!NOTE]
> All pull requests are subject to code review. Please be patient and responsive to any feedback or requests for changes.

## Coding Guidelines
### Style Guide
- **Language**: Java 17
- **Formatting**: Make sure to import the [code style](/code_style/eclipse_codestyle.xml) file.
- **Naming Conventions**: Use meaningful and descriptive names for classes, methods, and variables. In general adhere to clean code.
- **Documentation**: Write Javadoc comments for public classes and methods.

### Documentation
Ensure that any new features or changes are well-documented:
- Update existing documentation if necessary be it in Javadocs or in the [hateoflux-documentation](https://github.com/kamillionlabs/hateoflux-documentation).
- Add new sections to the documentation for significant changes.
- Provide examples or usage instructions where applicable.

### Testing
- Unit-Tests: Write unit tests for new features or bug fixes.
- Coverage: Strive for high test coverage to maintain code quality. In general, the coverage percentage shouldn't go down.

## Issue Guidelines
When creating issues, please adhere to the following guidelines to help maintain clarity and efficiency:
- **Be Descriptive**: Provide a clear title and a detailed description.
- **Reproduce**: Include steps to reproduce the issue.
- **Environment**: Specify the environment details such as Java version, Spring version, and any other relevant configurations.
- **Logs**: Attach relevant log snippets or error messages.

## License
By contributing to hateoflux, you agree that your contributions will be licensed under the [Apache License 2.0](/LICENSE).

## Contact
If you have any questions, suggestions, or need support, please feel free to open a [discussion](https://github.com/kamillionlabs/hateoflux/discussions), submit an [issue](https://github.com/kamillionlabs/hateoflux/issues), or email us directly at [contact@kamillionlabs.de](mailto:contact@kamillionlabs.de).