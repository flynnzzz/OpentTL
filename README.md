<br/>

<div align="center">
  <img src="./src/main/resources/logo.png" width="120" alt="OpenTL Logo">
</div>

<br/>
<h3 align="center">
  A simple and straightforward Java-developed Tier List Maker
</h3>
<br/>

<div align="center">
  <img src="./assets/tier_list_example.png" alt="Tier List Example">
</div>

<div align="center">
  <p>
    <i>graphical interface built manually using the JavaFX library<i/>
  </p> 
</div>

## Features

- **Visual tier list creation** — Drag and drop items into tier categories
- **Customizable tiers** — Add, remove, and rename tiers as needed
- **Mouse-based interaction** — Intuitive point-and-click controls
- **Exporting tier list as PNG** — Save your tier lists on disk with the export feature 

---

## Requirements

- **Java 25+**

---

## Installation

### Quick Start

**Download the latest release for your platform** from the [releases](https://github.com/flynnzzz/OpenTierList/releases) page. The release includes:

- The application JAR file
- Required JavaFX runtime JARs and binaries
- The `otl.sh` or `otl.bat` script for easy execution depending on your system

Once downloaded, run the application using the provided run script in the terminal:

```bash
./otl.sh
```
*On Linux systems*

or 

```batch
./otl.bat
```
*On Windows systems*

### Running the Downloaded JAR Directly

If you prefer to run the JAR without the script:

```bash
java --enable-native-access=javafx.graphics --module-path lib --add-modules javafx.controls -jar target/opentierlist.jar
```

---

## Building with Maven

### Prerequisites

- **Java 25+**
- **Maven 3.6+**

### Clone and Build

1. **Clone the repository:**

  ```bash
  git clone https://github.com/flynnzzz/OpenTL.git
  cd OpenTL
  ```

1. **Compile the project:**

  ```bash
  mvn clean compile
  ```

### Running the Application

After compilation, you can run the application in two ways:

**Option 1: Using Maven directly**

```bash
mvn javafx:run
```

**Option 2: Package and run as a JAR**

```bash
mvn package

# ... run options
```

The compiled JAR will be available in the `./target` directory.

---

## Supported formats

The following **image formats** are supported for imported tier list entries:

- *.png*
- *.jpg*
- *.jpeg*
- *.gif*

Tier Lists can be **saved** and later **loaded** as *.json* files (*.tson*).

---

## Development Notes

This project was created as a **learning exercise** to explore:

- JavaFX for GUI development
- Event handling and mouse interactions
- MVC architecture patterns
- Maven build automation

---

## TODOs and possible improvements

- Keyboard navigation and shortcuts
- Undo/redo functionality
- Better UI / model separation

---
