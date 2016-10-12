# -*- coding: utf-8 -*-
"""
Created on Sun May 29 16:41:59 2016

@author: Senjuti Kundu
"""

from bs4 import BeautifulSoup
import urllib
import re

tutorial_name = "Android.html"
url="https://developer.android.com/training/index.html"
all_links = ["https://developer.android.com/training/index.html",
"https://developer.android.com/training/basics/firstapp/index.html",
"https://developer.android.com/training/basics/firstapp/creating-project.html",
"https://developer.android.com/training/basics/firstapp/running-app.html",
"https://developer.android.com/training/basics/firstapp/building-ui.html",
"https://developer.android.com/training/basics/starting-activity.html",
"https://developer.android.com/training/basics/supporting-devices/index.html",
"https://developer.android.com/training/basics/supporting-devices/languages.html",
"https://developer.android.com/training/basics/supporting-devices/screens.html",
"https://developer.android.com/training/basics/supporting-devices/platforms.html",
"https://developer.android.com/training/basics/activity-lifecycle/index.html",
"https://developer.android.com/training/basics/activity-lifecycle/starting.html",
"https://developer.android.com/training/basics/activity-lifecycle/pausing.html",
"https://developer.android.com/training/basics/activity-lifecycle/stopping.html",
"https://developer.android.com/training/basics/activity-lifecycle/recreating.html",
"https://developer.android.com/training/basics/fragments/index.html",
"https://developer.android.com/training/basics/fragments/creating.html",
"https://developer.android.com/training/basics/fragments/fragment-ui.html",
"https://developer.android.com/training/basics/fragments/communicating.html",
"https://developer.android.com/training/basics/data-storage/index.html",
"https://developer.android.com/training/basics/data-storage/shared-preferences.html",
"https://developer.android.com/training/basics/data-storage/files.html",
"https://developer.android.com/training/basics/data-storage/databases.html",
"https://developer.android.com/training/basics/intents/index.html",
"https://developer.android.com/training/basics/intents/sending.html",
"https://developer.android.com/training/basics/intents/result.html",
"https://developer.android.com/training/basics/intents/filters.html",
"https://developer.android.com/training/permissions/index.html",
"https://developer.android.com/training/building-content-sharing.html",
"https://developer.android.com/training/sharing/index.html",
"https://developer.android.com/training/sharing/send.html",
"https://developer.android.com/training/sharing/receive.html",
"https://developer.android.com/training/sharing/shareaction.html",
"https://developer.android.com/training/secure-file-sharing/index.html",
"https://developer.android.com/training/secure-file-sharing/setup-sharing.html",
"https://developer.android.com/training/secure-file-sharing/share-file.html",
"https://developer.android.com/training/secure-file-sharing/request-file.html",
"https://developer.android.com/training/secure-file-sharing/retrieve-info.html",
"https://developer.android.com/training/beam-files/index.html",
"https://developer.android.com/training/beam-files/send-files.html",
"https://developer.android.com/training/beam-files/receive-files.html",
"https://developer.android.com/training/building-multimedia.html",
"https://developer.android.com/training/managing-audio/index.html",
"https://developer.android.com/training/managing-audio/volume-playback.html",
"https://developer.android.com/training/managing-audio/audio-focus.html",
"https://developer.android.com/training/managing-audio/audio-output.html",
"https://developer.android.com/training/camera/index.html",
"https://developer.android.com/training/camera/photobasics.html",
"https://developer.android.com/training/camera/videobasics.html",
"https://developer.android.com/training/camera/cameradirect.html",
"https://developer.android.com/training/printing/index.html",
"https://developer.android.com/training/building-graphics.html",
"https://developer.android.com/training/displaying-bitmaps/index.html",
"https://developer.android.com/training/displaying-bitmaps/load-bitmap.html",
"https://developer.android.com/training/displaying-bitmaps/process-bitmap.html",
"https://developer.android.com/training/displaying-bitmaps/cache-bitmap.html",
"https://developer.android.com/training/displaying-bitmaps/manage-memory.html",
"https://developer.android.com/training/displaying-bitmaps/display-bitmap.html",
"https://developer.android.com/training/graphics/opengl/index.html",
"https://developer.android.com/training/graphics/opengl/environment.html",
"https://developer.android.com/training/graphics/opengl/shapes.html",
"https://developer.android.com/training/graphics/opengl/draw.html",
"https://developer.android.com/training/graphics/opengl/projection.html",
"https://developer.android.com/training/graphics/opengl/motion.html",
"https://developer.android.com/training/graphics/opengl/touch.html",
"https://developer.android.com/training/transitions/index.html",
"https://developer.android.com/training/animation/index.html",
"https://developer.android.com/training/animation/crossfade.html",
"https://developer.android.com/training/animation/screen-slide.html",
"https://developer.android.com/training/animation/cardflip.html",
"https://developer.android.com/training/animation/zoom.html",
"https://developer.android.com/training/animation/layout.html",
"https://developer.android.com/training/building-connectivity.html",
"https://developer.android.com/training/connect-devices-wirelessly/index.html",
"https://developer.android.com/training/basics/network-ops/index.html",
"https://developer.android.com/training/basics/network-ops/connecting.html",
"https://developer.android.com/training/basics/network-ops/managing.html",
"https://developer.android.com/training/basics/network-ops/xml.html",
"https://developer.android.com/training/efficient-downloads/index.html",
"https://developer.android.com/training/efficient-downloads/efficient-network-access.html",
"https://developer.android.com/training/efficient-downloads/regular_updates.html",
"https://developer.android.com/training/efficient-downloads/redundant_redundant.html",
"https://developer.android.com/training/efficient-downloads/connectivity_patterns.html",
"https://developer.android.com/training/backup/index.html",
"https://developer.android.com/training/sync-adapters/index.html",
"https://developer.android.com/training/sync-adapters/creating-authenticator.html",
"https://developer.android.com/training/sync-adapters/creating-stub-provider.html",
"https://developer.android.com/training/sync-adapters/creating-sync-adapter.html",
"https://developer.android.com/training/sync-adapters/running-sync-adapter.html",
"https://developer.android.com/training/volley/index.html",
"https://developer.android.com/training/building-location.html",
"https://developer.android.com/training/location/index.html",
"https://developer.android.com/training/location/retrieve-current.html",
"https://developer.android.com/training/location/change-location-settings.html",
"https://developer.android.com/training/location/receive-location-updates.html",
"https://developer.android.com/training/location/display-address.html",
"https://developer.android.com/training/location/geofencing.html",
"https://developer.android.com/training/building-userinfo.html",
"https://developer.android.com/training/contacts-provider/index.html",
"https://developer.android.com/training/contacts-provider/retrieve-names.html",
"https://developer.android.com/training/contacts-provider/retrieve-details.html",
"https://developer.android.com/training/contacts-provider/modify-data.html",
"https://developer.android.com/training/contacts-provider/display-contact-badge.html",
"https://developer.android.com/training/building-wearables.html",
"https://developer.android.com/training/wearables/notifications/index.html",
"https://developer.android.com/training/wearables/apps/index.html",
"https://developer.android.com/training/wearables/ui/index.html",
"https://developer.android.com/training/wearables/data-layer/index.html",
"https://developer.android.com/training/wearables/watch-faces/index.html",
"https://developer.android.com/training/tv/index.html",
"https://developer.android.com/training/tv/start/index.html",
"https://developer.android.com/training/tv/playback/index.html",
"https://developer.android.com/training/tv/playback/browse.html",
"https://developer.android.com/training/tv/playback/details.html",
"https://developer.android.com/training/tv/playback/details.html",
"https://developer.android.com/training/tv/playback/now-playing.html",
"https://developer.android.com/training/tv/playback/guided-step.html",
"https://developer.android.com/training/tv/playback/options.html",
"https://developer.android.com/training/tv/discovery/index.html",
"https://developer.android.com/training/tv/discovery/recommendations.html",
"https://developer.android.com/training/tv/discovery/searchable.html",
"https://developer.android.com/training/tv/discovery/in-app-search.html",
"https://developer.android.com/training/tv/tif/index.html",
"https://developer.android.com/training/tv/tif/tvinput.html",
"https://developer.android.com/training/tv/tif/channel.html",
"https://developer.android.com/training/tv/tif/ui.html",
"https://developer.android.com/training/auto/index.html",
"https://developer.android.com/training/best-ux.html",
"https://developer.android.com/training/design-navigation/index.html",
"https://developer.android.com/training/implementing-navigation/index.html",
"https://developer.android.com/training/notify-user/index.html",
"https://developer.android.com/training/swipe/index.html",
"https://developer.android.com/training/swipe/add-swipe-interface.html",
"https://developer.android.com/training/swipe/respond-refresh-request.html",
"https://developer.android.com/training/search/index.html",
"https://developer.android.com/training/search/setup.html",
"https://developer.android.com/training/search/search.html",
"https://developer.android.com/training/search/backward-compat.html",
"https://developer.android.com/training/app-indexing/index.html",
"https://developer.android.com/training/app-indexing/deep-linking.html",
"https://developer.android.com/training/app-indexing/enabling-app-indexing.html",
"https://developer.android.com/training/best-ui.html",
"https://developer.android.com/training/multiscreen/index.html",
"https://developer.android.com/training/multiscreen/screensizes.html",
"https://developer.android.com/training/multiscreen/screendensities.html",
"https://developer.android.com/training/multiscreen/adaptui.html",
"https://developer.android.com/training/appbar/index.html",
"https://developer.android.com/training/appbar/setting-up.html",
"https://developer.android.com/training/appbar/actions.html",
"https://developer.android.com/training/appbar/up-action.html",
"https://developer.android.com/training/appbar/action-views.html",
"https://developer.android.com/training/snackbar/index.html",
"https://developer.android.com/training/snackbar/showing.html",
"https://developer.android.com/training/snackbar/action.html",
"https://developer.android.com/training/custom-views/index.html",
"https://developer.android.com/training/custom-views/create-view.html",
"https://developer.android.com/training/custom-views/custom-drawing.html",
"https://developer.android.com/training/custom-views/making-interactive.html",
"https://developer.android.com/training/custom-views/optimizing-view.html",
"https://developer.android.com/training/backward-compatible-ui/index.html",
"https://developer.android.com/training/accessibility/index.html",
"https://developer.android.com/training/accessibility/accessible-app.html",
"https://developer.android.com/training/accessibility/service.html",
"https://developer.android.com/training/accessibility/testing.html",
"https://developer.android.com/training/system-ui/index.html",
"https://developer.android.com/training/material/index.html",
"https://developer.android.com/training/best-user-input.html",
"https://developer.android.com/training/gestures/index.html",
"https://developer.android.com/training/keyboard-input/index.html",
"https://developer.android.com/training/keyboard-input/style.html",
"https://developer.android.com/training/keyboard-input/visibility.html",
"https://developer.android.com/training/keyboard-input/navigation.html",
"https://developer.android.com/training/keyboard-input/commands.html",
"https://developer.android.com/training/game-controllers/index.html",
"https://developer.android.com/training/game-controllers/controller-input.html",
"https://developer.android.com/training/game-controllers/compatibility.html",
"https://developer.android.com/training/game-controllers/multiple-controllers.html",
"https://developer.android.com/training/best-background.html",
"https://developer.android.com/training/run-background-service/index.html",
"https://developer.android.com/training/run-background-service/create-service.html",
"https://developer.android.com/training/run-background-service/send-request.html",
"https://developer.android.com/training/run-background-service/report-status.html",
"https://developer.android.com/training/load-data-background/index.html",
"https://developer.android.com/training/scheduling/index.html",
"https://developer.android.com/training/best-performance.html",
"https://developer.android.com/training/improving-layouts/index.html",
"https://developer.android.com/training/improving-layouts/optimizing-layout.html",
"https://developer.android.com/training/improving-layouts/reusing-layouts.html",
"https://developer.android.com/training/improving-layouts/loading-ondemand.html",
"https://developer.android.com/training/improving-layouts/smooth-scrolling.html",
"https://developer.android.com/training/monitoring-device-state/index.html",
"https://developer.android.com/training/performance/battery/network/index.html",
"https://developer.android.com/training/monitoring-device-state/doze-standby.html",
"https://developer.android.com/training/monitoring-device-state/battery-monitoring.html",
"https://developer.android.com/training/monitoring-device-state/docking-monitoring.html",
"https://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html",
"https://developer.android.com/training/monitoring-device-state/manifest-receivers.html",
"https://developer.android.com/training/performance/battery/network/index.html",
"https://developer.android.com/training/multiple-threads/index.html",
"https://developer.android.com/training/multiple-threads/define-runnable.html",
"https://developer.android.com/training/multiple-threads/create-threadpool.html",
"https://developer.android.com/training/multiple-threads/run-code.html",
"https://developer.android.com/training/multiple-threads/communicate-ui.html",
"https://developer.android.com/training/best-security.html",
"https://developer.android.com/training/best-permissions-ids.html",
"https://developer.android.com/training/testing/index.html",
"https://developer.android.com/training/testing/unit-testing/index.html",
"https://developer.android.com/training/testing/ui-testing/index.html",
"https://developer.android.com/training/testing/integration-testing/index.html",
"https://developer.android.com/training/distribute.html",
"https://developer.android.com/training/in-app-billing/index.html",
"https://developer.android.com/training/in-app-billing/preparing-iab-app.html",
"https://developer.android.com/training/in-app-billing/list-iab-products.html",
"https://developer.android.com/training/in-app-billing/purchase-iab-products.html",
"https://developer.android.com/training/in-app-billing/test-iab-app.html",
"https://developer.android.com/training/multiple-apks/index.html",
"https://developer.android.com/training/multiple-apks/api.html",
"https://developer.android.com/training/multiple-apks/screensize.html",
"https://developer.android.com/training/multiple-apks/texture.html",
"https://developer.android.com/training/multiple-apks/multiple.html",]

for a in all_links:
	page = urllib.urlopen(a)
	page_content = page.read()
	with open(tutorial_name, 'a') as fid:
		fid.write(page_content)


            
    
