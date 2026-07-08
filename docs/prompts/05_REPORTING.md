# Prompt — Reporting List and Report Editor

Implement reporting from real Room data using `reporting_screen` and `new_report_screen` references.

Reporting list: title, plus action, search, All Reports/Draft/Exported/Shared chips, cards with real title/location/date, attachment thumbnails/counts, status badge, and share action. Plus creates a persistent draft before navigation.

New/Edit Report: durable auto-save by report ID; title, report location refresh, date, time, notes, ordered attachments selected from Collection, GPS summary, Save Draft, and Submit/Export. Restore after rotation/process death. Use the documented GPS priority rule and never invent coordinates. Validate title and attachment requirements.

Implement status transitions from real export/share events. Add DAO/repository/domain tests, UI tests, draft process-death tests, filter/search tests, and screenshot comparisons. Do not hardcode the sample reports shown in the design.
