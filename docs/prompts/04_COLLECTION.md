# Prompt — Collection

Implement the Collection destination to match `collection_screen` and the reference screenshot using only real Room data.

Include top bar, search field, filter chips All/Today/This Week/Videos/Photos, date-grouped adaptive grid, video indicator, location label, processing/failed/missing states, and the shared bottom navigation. Use stable keys and size-aware thumbnails; never decode full-resolution media in the grid.

Create composable query filters in DAO/repository. Search must be debounced/cancellable and combine with filters. New captures appear reactively. Add media detail, long-press selection, secure share, delete confirmation and report-attachment handling. Repair/remove broken URI behavior must be explicit.

Test date boundaries/timezones, filter combinations, empty/loading/error states, large datasets, missing URIs, deletion, sharing, RTL, large font, and screenshot parity. Profile scrolling/query performance with realistic fixtures. Update task evidence.
