# A plural noun should be used for collection or store names

## Category

URIs

## Importance, severity, difficulty

* Importance: high
* Severity: error
* Difficulty to implement the rule: difficult

## Quality Attribute

* Usability
* Maintainability

## Rule Description

Description from Massé [1].

"A URI representing a document resource should be named with a singular noun or
noun phrase path segment."

## Implemented

* Y

## Implementation Details (Issue #15)

### What is checked

* Currently, static implementation only
* Check every pathSegment which are generated from the split of the path using the "/" as delimiter. If the pathSegment is a
  collection or store name, it should be plural. If it is not plural, it is a violation.
* The structure of a path is normally as follows: singular/plural/singular.. or plural/singular/plural.. . If we have a path with singular/singular then we have a violation.
* For each pathSegment is being checked if the word is singular or plural. For this taks we are using OpenNLP, an Apache library, which is able to detect if a word is singular or plural.

### What is not checked

* The semantic of rule is not checked, only the syntax following the defined schema.
### Future work

* Dynamic analysis will check the parameter input if it is plural or singular
* If a path segment is composed of more than one word, then we need to only take the last one into consideration
## Source

[1] https://www.oreilly.com/library/view/rest-api-design/9781449317904/
