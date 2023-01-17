REST API Specification Report
=============================
| Line No. | Line                                | Rule Violated                                                                    | Category | Severity | Rule Type | Software Quality Attributes    | Improvement Suggestion                                                                                          |
| -------- | ----------------------------------- | -------------------------------------------------------------------------------- | -------- | -------- | --------- | ------------------------------ | --------------------------------------------------------------------------------------------------------------- |
| 16       | /customers/{id}/orders.xml          | File extensions should not be included in URIs                                   | URIS     | ERROR    | STATIC    | MAINTAINABILITY                | To indicate the format of a message's entity body (XML) rely on the media type inside the Content-Type header.  |
| 16       | /customers/{id}/orders.xml          | Forward slash separator (/) must be used to indicate a hierarchical relationship | URIS     | CRITICAL | STATIC    | MAINTAINABILITY                | replace '.' with a forward slash '/' to indicate a hierarchical relationship                                    |
| 16       | /customers/{id}/orders.xml          | Hyphens (-) should be used to improve the readability of URIs                    | URIS     | ERROR    | STATIC    | COMPATIBILITY, MAINTAINABILITY | Use hyphens to improve the readability of the segments                                                          |
| 50       | /customers/{id}/orders.json         | File extensions should not be included in URIs                                   | URIS     | ERROR    | STATIC    | MAINTAINABILITY                | To indicate the format of a message's entity body (JSON) rely on the media type inside the Content-Type header. |
| 50       | /customers/{id}/orders.json         | Forward slash separator (/) must be used to indicate a hierarchical relationship | URIS     | CRITICAL | STATIC    | MAINTAINABILITY                | replace '.' with a forward slash '/' to indicate a hierarchical relationship                                    |
| 50       | /customers/{id}/orders.json         | Hyphens (-) should be used to improve the readability of URIs                    | URIS     | ERROR    | STATIC    | COMPATIBILITY, MAINTAINABILITY | Use hyphens to improve the readability of the segments                                                          |
| 84       | /customers/{id}/orders.html         | File extensions should not be included in URIs                                   | URIS     | ERROR    | STATIC    | MAINTAINABILITY                | To indicate the format of a message's entity body (HTML) rely on the media type inside the Content-Type header. |
| 84       | /customers/{id}/orders.html         | Forward slash separator (/) must be used to indicate a hierarchical relationship | URIS     | CRITICAL | STATIC    | MAINTAINABILITY                | replace '.' with a forward slash '/' to indicate a hierarchical relationship                                    |
| 84       | /customers/{id}/orders.html         | Hyphens (-) should be used to improve the readability of URIs                    | URIS     | ERROR    | STATIC    | COMPATIBILITY, MAINTAINABILITY | Use hyphens to improve the readability of the segments                                                          |
| 118      | /customers/{id}/orders.pdf          | File extensions should not be included in URIs                                   | URIS     | ERROR    | STATIC    | MAINTAINABILITY                | To indicate the format of a message's entity body (PDF) rely on the media type inside the Content-Type header.  |
| 118      | /customers/{id}/orders.pdf          | Forward slash separator (/) must be used to indicate a hierarchical relationship | URIS     | CRITICAL | STATIC    | MAINTAINABILITY                | replace '.' with a forward slash '/' to indicate a hierarchical relationship                                    |
| 118      | /customers/{id}/orders.pdf          | Hyphens (-) should be used to improve the readability of URIs                    | URIS     | ERROR    | STATIC    | COMPATIBILITY, MAINTAINABILITY | Use hyphens to improve the readability of the segments                                                          |
| 221      | /customers/{id}/orders.pdf/download | File extensions should not be included in URIs                                   | URIS     | ERROR    | STATIC    | MAINTAINABILITY                | To indicate the format of a message's entity body (PDF) rely on the media type inside the Content-Type header.  |
| 221      | /customers/{id}/orders.pdf/download | Forward slash separator (/) must be used to indicate a hierarchical relationship | URIS     | CRITICAL | STATIC    | MAINTAINABILITY                | replace '.' with a forward slash '/' to indicate a hierarchical relationship                                    |
| 221      | /customers/{id}/orders.pdf/download | Hyphens (-) should be used to improve the readability of URIs                    | URIS     | ERROR    | STATIC    | COMPATIBILITY, MAINTAINABILITY | Use hyphens to improve the readability of the segments                                                          |
| 256      | /customers/{id}/orders.heic         | Forward slash separator (/) must be used to indicate a hierarchical relationship | URIS     | CRITICAL | STATIC    | MAINTAINABILITY                | replace '.' with a forward slash '/' to indicate a hierarchical relationship                                    |
| 256      | /customers/{id}/orders.heic         | Hyphens (-) should be used to improve the readability of URIs                    | URIS     | ERROR    | STATIC    | COMPATIBILITY, MAINTAINABILITY | Use hyphens to improve the readability of the segments                                                          |