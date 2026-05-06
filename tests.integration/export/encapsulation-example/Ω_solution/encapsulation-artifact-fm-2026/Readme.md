# A Framework for the Interoperable Specification and Verification of Encapsulated Data Structures (Artifact)

This is the artifact for the FM 2026 paper "A Framework for the Interoperable Specification and Verification of Encapsulated Data Structures" by Wolfram Pfeifer, Werner Dietl, and Mattias Ulbrich. It contains the example project described in the paper, which consists of a Java client using three libraries (Cell, LinkedCellList, and IntTreeSet), verified cooperatively with the tools KeY (standard variant using Dynamic Frames), VeriFast, and a Universe Type Checker plus KeY (special variant using Universe Types). The artifact provides the source code of the example, specifications in the different involved languages, as well as proofs that can be reloaded (if the specific verification tool supports that).

## Requirements

The full proof replay with the Docker container requires a machine with an amd64 architecture, since the involved tool VeriFast does not provide ARM binaries (despite quite some effort, we did not succeed in building Linux ARM binaries). However, everything except for VeriFast can be run also through our Docker container for ARM, and macOS ARM binaries for VeriFast are included in the artifact and might be used (without Docker). Other operating systems on ARM are not supported.

For each involved verification tool, scripts/commands are provided to reload/check the corresponding proofs. Java 21 or newer is needed by the tools (both KeY variants, Universe Type Checker, and citool), while VeriFast needs the package libgomp1 apart from those that are installed in a minimal Ubuntu 24.04 distribution. For convenience, we provide a Docker image that can be used to run the tools. The proof replay runs on any standard machine with at least 1 GB of RAM (tested with `docker run -m 1g --memory-swap 1g --cpus=1`), and should need about 4 min for checking all the proofs.

### Involved Tools

The tools can be found as binaries/JARs in the `tools` folder.

* KeY (default Dynamic Frames version; https://github.com/KeYProject/key)
* KeY (special variant using Universe Encapsulation Types; https://github.com/KeYProject/key/tree/pfeifer/universeEncapsulation)
* citool (provides a command-line interface for the GUI tool KeY; https://github.com/wadoon/key-citool)
* VeriFast (https://github.com/verifast/verifast)
* Checker for Universe Encapsulation Types (implemented in the EISOP Checker Framework; https://github.com/WolframPfeifer/universe/tree/pfeifer/encapsulation)
* Contract-Chameleon (for generating interfaces and stubs from the Contract-LIB specification; https://github.com/Contract-LIB/contract-chameleon)

## Installation and Smoke Tests

Make sure that Docker is installed:
* [Linux](https://docs.docker.com/desktop/install/linux-install/)
* [Apple](https://docs.docker.com/desktop/install/mac-install/)
* [Windows](https://docs.docker.com/desktop/install/windows-install/)

Make sure that you are in the directory of the artifact (where this Readme is located).

The Docker image required to run the tools in this artifact can be downloaded from DockerHub, which should happen automatically with `docker run` if you have internet connection.
Alternatively, it can also be loaded from the image file contained in the artifact.
To load it from the file on a machine with amd64 architecture, use the following command:
```bash
docker load -i docker/wolframpfeifer_encapsulation_amd64.tar.gz
```
Respectively, for ARM architecture:
```bash
docker load -i docker/wolframpfeifer_encapsulation_arm64.tar.gz
```

Afterwards the image `wolframpfeifer/encapsulation` will be available locally for running containers from it.

The smoke tests can be run using the following command:
```bash
docker run -v .:/mnt/enc wolframpfeifer/encapsulation ./smokeTest.sh
```

If you have macOS on ARM, run:
```bash
docker run -v .:/mnt/enc wolframpfeifer/encapsulation ./smokeTestNoVeriFast.sh
```
Then call VeriFast directly:
```bash
tools/vf/verifast-26.01-macos-aarch/bin/verifast --help
```
This should print (parts of) the help messages of the corresponding tools (VeriFast, KeY, the Universe Encapsulation Types (UET) type checker, the KeY variant for Universe Types, and citool) and thus show that they can be executed correctly.

Note that the current directory is mounted into the Docker container via `-v .:/mnt/enc`, so changes outside the container are directly in effect inside and vice versa.

## Workflow of the Cooperative Verification Technique

Our technique is applicable to verify programs where a client uses multiple data structures that are encapsulated (a precise formal definition of this notion is given in the paper).
The following workflow can be used:

* The user provides abstract specifications of the functionality of the data structure classes in the language Contract-LIB.
* The user provides an implementation of the client class using the data structures.
* The tool Contract-Chameleon is used to generate the following files for each data structure:
    1. a verification template with the interface and the specification of the data structure to be proven (depending on the technique that should be used for verification of that data structure)
    2. a verification stub (classes inheriting from the template from (1)), where the user needs to fill in implementation and additional specification (such as coupling invariants/predicates, loop invariants, ...)
    3. a verification interface containing the necessary specification to use the data structure for verification in the client (depending on the technique that should be used for verifying the client) or for use in other verification templates
* The filled-in stubs are then verified w.r.t. the verification templates (1), each with the concrete verification technique/tool chosen for it.
* The client is verified in the technique of choice, using the verification interfaces (3).

Note that at the moment, Contract-Chameleon is still a prototype and only supports export for some of the verification tools. Therefore, after generation of the language/tool-specific interfaces and stubs (step 3), manual changes had to be made.

## Structure of this Example

The example consists of a client class that uses multiple data structures: a cell class (small mutable container; containing an int in this case), a linked list of cells, and a set of ints, implemented as a binary tree.
Verification is conducted cooperatively with three different tools: VeriFast, KeY, and a type checker for Universe Encapsulation Types (a variant of the Universe Type System) implemented in the Checker Framework.

The different variants of the source files and specifications for each verification tool can be found in the correspondingly named subfolders.

Files in the artifact:
```
* client+key-interfaces
    * Client.java                           (client implementation, main verification target)
    * Cell.java                             (verification interface for class Cell)
    * IntTreeSet.java                       (verification interface for class IntTreeSet)
    * LinkedCellList.java                   (verification interface for class LinkedCellList)
    * *.proof                               (KeY proof of the client, uses the verification interfaces)
* contractlib
    * LinkedCellList.clib                   (abstract specification of Cell and LinkedCellList)
    * IntTreeSet.clib                       (abstract specification of IntTreeSet)
* docker
    * Dockerfile                            (file for building the Docker image)
    * wolframpfeifer_encapsulation.tar.gz   (docker image for running the proof checking tools)
* key
    * Cell.java                             (verification template for Cell, contains specs)
    * CellImpl.java                         (verification stub, filled in by the user with implementation and additional specification)
    * *.proof                               (KeY proofs that CellImpl adheres to specs in Cell)
* universe
    * qual                                  (contains definitions of the UET annotations, used by KeY for parsing)
        * Any.java
        * Payload.java
        * Rep.java
        * ...
    * IntTreeSet.java                       (verification template for IntTreeSet, contains specs)
    * IntTreeSetImpl.java                   (verification stub, filled in by the user with implementation and additional specification)
    * TreeNode.java                         (used internally by IntTreeSetImpl as part of the set implementation)
    * *.proof                               (KeY proofs that CellImpl adheres to specs in Cell)
* verifast
    * LinkedCellList.java                   (verification template for LinkedCellList, contains specs)
    * LinkedCellListImpl.java               (verification stub, filled in by the user with implementation and additional specification)
    * Cell.javaspec                         (verification interface for class Cell)
    * cell.jarspec                          ("bundle" for the specification of Cell, needed by VeriFast when referring to javaspec files)
    * sources.jarsrc                        (entry file for verification with VeriFast)
* tools
    * key-2.12.4-dev-exe.jar                (KeY build using Dynamic Frames)
    * key-2.12.4-UT-dev-exe.jar             (KeY build using Universe Encapsulation Types)
    * citool-1.7.0-SNAPSHOT-mini.jar        (tool that provides a CLI for KeY)
    * contract-chameleon-exe.jar            (Contract-Chameleon for generating interfaces and stubs from Contract-LIB specifications)
    * uet-checker
        * checkEnc.sh                       (script to run the UET checker)
        * ...
    * vf
        * verifast-26.01-linux-amd64        (binaries and runtime files for VeriFast, runnable on Linux amd64)
        * verifast-26.01-macos-aarch        (binaries and runtime files for VeriFast, runnable on MacOS ARM)
* check.sh                                  (top-level check script loading all proofs / re-running all the verifiers)
* checkNoVeriFast.sh                        (script for checking everything except for VeriFast proofs)
* smokeTests.sh                             (script for running smoke tests)
* smokeTestNoVeriFast.sh                    (script for running smoke tests w/o VeriFast)
* Readme.md                                 (this file)
```

## Replaying/Checking the Proofs

We provide a script as the main task for running the tools and replaying/checking the proofs.
Make sure that the current location is the main directory of the artifact and run the script in `check.sh` via the provided Docker container:
```bash
docker run -v .:/mnt/enc wolframpfeifer/encapsulation ./check.sh
```

On macOS ARM, run everything except for VeriFast through Docker:
```bash
docker run -v .:/mnt/enc wolframpfeifer/encapsulation ./checkNoVeriFast.sh
tools/vf/verifast-26.01-macos-aarch/bin/verifast -c -allow_dead_code -shared verifast/sources.jarsrc
```

### Manually Running the Checks (Optional)

For experimenting and understanding, this section explains how each individual tool is called.

#### Verification with VeriFast (Separation Logic)

Run VeriFast on the provided file to check all assertions/contracts to (re-)verify the class `IntTreeSet` (and its nested data structure class `TreeNode`).
With amd64 architecture:
```bash
docker run -v .:/mnt/enc wolframpfeifer/encapsulation 'tools/vf/verifast-26.01-linux-amd64/bin/verifast -c -allow_dead_code -shared verifast/sources.jarsrc'
```
If you have macOS on ARM, you can run the provided VeriFast binary for macOS ARM without Docker:
```bash
tools/vf/verifast-26.01-macos-aarch/bin/verifast -c -allow_dead_code -shared verifast/sources.jarsrc
```
We run VeriFast with the following options:

* `-c` to only do the compilation and verification and skip the linking phase
* `-allow_dead_code` to skip checks for dead code, since some of the methods are not called
* `-shared` to mark that we verify a library and no main method is required

The call should succeed nearly immediately and print `0 errors found (36 statements verified)`.

#### Verification of the Client with KeY (Dynamic Frames)

Run the following command to check that all the provided proofs are loadable.
```bash
docker run -v .:/mnt/enc wolframpfeifer/encapsulation 'java -Dslf4j.internal.verbosity=ERROR -Dlogback.configurationFile=tools/disablelogging.xml -cp "tools/key-2.12.4-dev-exe.jar:tools/citool-1.7.0-SNAPSHOT-mini.jar" io.github.wadoon.keycitool.CheckerKt -v --proof-path client+key-interfaces client+key-interfaces'
```
The output should show 16 successfully closed proofs.

Note: We are using citool as a clean CLI for KeY, which is primarily a GUI tool.
It is also possible to use the GUI to load and inspect the proofs, which can be done by running the GUI of KeY without Docker (`java -jar tools/key-2.12.4-dev-exe.jar`) and manually selecting the proof to load via the menu.

#### Verification of the Cell with KeY (Dynamic Frames)

Run the following command to check that all the provided proofs are loadable.
```bash
docker run -v .:/mnt/enc wolframpfeifer/encapsulation 'java -Dslf4j.internal.verbosity=ERROR -Dlogback.configurationFile=tools/disablelogging.xml -cp "tools/key-2.12.4-dev-exe.jar:tools/citool-1.7.0-SNAPSHOT-mini.jar" io.github.wadoon.keycitool.CheckerKt -v --proof-path key key'
```
The output should show 9 successfully closed proofs.

#### Verification with Universe Encapsulation Types + KeY

First, it should be checked that the type checker runs without any errors on the provided program:
```bash
docker run -v .:/mnt/enc wolframpfeifer/encapsulation 'tools/uet-checker/checkEnc.sh universe/*.java'
```
This runs the type checker for Universe Encapsulation Types (a stricter variant of the Universe Type system found at https://github.com/opprop/universe), and checks that the involved Java classes adhere to the Universe Encapsulation Types and schema as described in the paper.
If everything works correctly, it prints the Java compiler version, the command used for compiling/type checking, and then no further output (type errors would reflect as compiler errors).
Note that the checker is based on the EISOP Checker Framework (https://eisop.github.io/cf/) and runs as an annotation processor during compilation with `javac`, and as a side effect the Java files are also compiled. However, in this verification project, we do not need the .class files.

Afterwards, for checking the functional specification, load the proofs with the following command:
```bash
docker run -v .:/mnt/enc wolframpfeifer/encapsulation 'java -Dslf4j.internal.verbosity=ERROR -Dlogback.configurationFile=tools/disablelogging.xml -cp "tools/key-2.12.4-UT-dev-exe.jar:tools/citool-1.7.0-SNAPSHOT-mini.jar" io.github.wadoon.keycitool.CheckerKt -v --proof-path universe universe'
```
Note that this runs a different KeY variant than the one used with Dynamic Frames. This variant makes use of the type and effect annotations verified correct in the first step, and exploits this information to simplify the framing proofs significantly.
The printed output should show 15 successfully closed proofs.

## Generating Interfaces and Stubs with Contract-Chameleon

As mentioned above, Contract-Chameleon (https://github.com/Contract-LIB/contract-chameleon) is still a prototype with only partial support for some of our use cases.
We used it where possible to create verification interfaces and stubs, but some changes to the output had to be made manually.
Beware that regenerating the verification interfaces and templates might overwrite these manual changes.

In principle, no manual changes are needed, but the tool does currently not have all the features needed for this case study.
Likewise, be careful that regenerating the stubs (*Impl.java files) might overwrite all implementations and additional specifications added there by the user.
However, if you want to try it out anyways, an executable JAR can be found in the tools folder.

### Client
The verification interfaces for use in client verification are generated with the following commands:
```bash
# The input file LinkedCellList.clib also contains Cell, since LinkedCellList depends on it ...
docker run -v .:/mnt/enc wolframpfeifer/encapsulation 'java -jar tools/contract-chameleon-exe.jar key-applicant contractlib/LinkedCellList.clib -o gen_client'
docker run -v .:/mnt/enc wolframpfeifer/encapsulation 'java -jar tools/contract-chameleon-exe.jar key-applicant contractlib/IntTreeSet.clib -o gen_client'
```
This generates a folder `gen_client` with subfolders corresponding to the input files, where the generated stubs can be found.
Note that at the moment always all the files are generated (in this case also a verification interface for `Cell.java`, it is not yet possible to filter.
The files in `client+key-interfaces` are based on those (with a few minor changes).

### KeY (Dynamic Frames variant):

```bash
# The input file LinkedCellList.clib also contains Cell, since LinkedCellList depends on it ...
docker run -v .:/mnt/enc wolframpfeifer/encapsulation 'java -jar tools/contract-chameleon-exe.jar key-provider contractlib/LinkedCellList.clib -o gen_key'
```
This generates among others the files `Cell.java` and `CellImpl.java` inside of `gen_key`, which are the base for the files in folder `key`.

### VeriFast
Generate the verification stubs and templates for VeriFast:
```bash
# The input file LinkedCellList.clib also contains Cell, since LinkedCellList depends on it ...
docker run -v .:/mnt/enc wolframpfeifer/encapsulation 'java -jar tools/contract-chameleon-exe.jar verifast-provider contractlib/LinkedCellList.clib -o gen_verifast'
docker run -v .:/mnt/enc wolframpfeifer/encapsulation 'java -jar tools/contract-chameleon-exe.jar verifast-applicant contractlib/LinkedCellList.clib -o gen_verifast'
```
The first command creates (among others) `LinkedCellList.java` and `LinkedCellListImpl.java` in the folder `gen_verifast`, the second one `Cell.javaspec`, which are the base for the corresponding files in the example.

Note that VeriFast support is very prototypical at the moment, so the files had to be edited by hand quite a bit. In particular, predicate families and additional functions (defined via "fixpoint" in VeriFast) are not supported at the moment.

### KeY + Universe Encapsulation Types

Support for the UET variant of KeY is not yet implemented, but ongoing work.

# Support

If you struggle with any installation or verification step, please reach out to [Wolfram Pfeifer](https://formal.kastel.kit.edu/~pfeifer/).
