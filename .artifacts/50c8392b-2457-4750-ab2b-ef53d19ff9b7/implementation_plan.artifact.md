# Implementation Plan - UI Enhancements and Clickstream Export

This plan addresses three requests:
1.  Remove the session event count from the top-right corner of the learning screens.
2.  Add a "Download Excel" feature to the Admin Dashboard (specifically for clickstream data).
3.  Implement an in-app YouTube video player to prevent external app redirection.

## User Review Required

> [!IMPORTANT]
> The "Download Excel" feature will generate a CSV file with an `.xls` or `.csv` extension, which is the standard way to export data for Excel without adding heavy external libraries like Apache POI.
> The YouTube player will use an embedded `WebView` to play videos directly within the app.

## Proposed Changes

### [UI Enhancement] - Remove Event Count
#### [MODIFY] [ReadingMaterialScreen.kt](file:///C:/Users/jiten/Desktop/Mtech/Mtech%202nd%20year/photosynthesis-lab---class-9/app/src/main/java/com/example/ui/screens/ReadingMaterialScreen.kt)
- Remove the `eventCount` state collection.
- Remove the UI `Surface` badge that displays the event count in the header.

---

### [Admin Dashboard] - Excel Export Feature
#### [MODIFY] [AnalyticsRepository.kt](file:///C:/Users/jiten/Desktop/Mtech/Mtech%202nd%20year/photosynthesis-lab---class-9/app/src/main/java/com/example/data/AnalyticsRepository.kt)
- Add `exportToExcelCsvString` method if specialized formatting is needed, otherwise use `exportToCsvString`.

#### [MODIFY] [ClickstreamExportDialog.kt](file:///C:/Users/jiten/Desktop/Mtech/Mtech%202nd%20year/photosynthesis-lab---class-9/app/src/main/java/com/example/ui/components/ClickstreamExportDialog.kt)
- Add support for "EXCEL" export type.
- Update the "Download" button logic to share the content as a file URI (using a temporary file) if possible, or ensure the MIME type is correctly set for Excel.

#### [MODIFY] [AdminDashboardScreen.kt](file:///C:/Users/jiten/Desktop/Mtech/Mtech%202nd%20year/photosynthesis-lab---class-9/app/src/main/java/com/example/ui/screens/AdminDashboardScreen.kt)
- Add a new "Download EXCEL" button alongside CSV and JSON options.
- Update the state handling for the export dialog to include the new Excel format.

---

### [Media] - In-app YouTube Player
#### [MODIFY] [MediaComponents.kt](file:///C:/Users/jiten/Desktop/Mtech/Mtech%202nd%20year/photosynthesis-lab---class-9/app/src/main/java/com/example/ui/components/MediaComponents.kt)
- Implement a `YouTubePlayerView` using `AndroidView` and `WebView`.
- Add a helper function to extract the YouTube video ID from a URL.
- Update `YouTubeVideoCard` to toggle between the thumbnail preview and the embedded player when clicked, instead of launching an external intent.

## Verification Plan

### Automated Tests
- I will run the existing tests to ensure no regressions.
- I will check if any unit tests depend on the removed `eventCount` (though highly unlikely for UI components).

### Manual Verification
- **Event Count:** Verify the top-right badge is gone in `ReadingMaterialScreen`.
- **Excel Download:** Verify a new button exists in Admin Dashboard and it triggers a share/download intent with CSV/Excel compatible data.
- **YouTube Player:** Verify that clicking a video card in `ReadingMaterialScreen` (Sections 3, 4, or 5) plays the video directly inside the app without opening the YouTube app.
