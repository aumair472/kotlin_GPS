# Permission Verification Matrix

| Camera | Location | Mic | Expected behavior |
|---|---|---|---|
| Granted | Precise | Granted | Full photo/video with audio and GPS |
| Granted | Approximate | Granted | Capture works; approximate indicator stored |
| Granted | Denied | Granted | Capture works with No GPS disclosure |
| Granted | Disabled provider | Granted | Capture works; location disabled action |
| Granted | Precise | Denied | Photos and silent video work |
| Denied | Any | Any | Camera unavailable; other destinations usable |
| Permanently denied | Any | Any | Settings action; no repeated blind prompt |
| Revoked while paused | Any | Any | Recheck, stop resource, recover UI |

Verify first denial, rationale, permanent denial, system settings return, approximate→precise and precise→approximate changes, and process recreation.
