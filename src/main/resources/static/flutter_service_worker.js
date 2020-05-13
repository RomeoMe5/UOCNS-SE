'use strict';
const CACHE_NAME = 'flutter-app-cache';
const RESOURCES = {
  "assets/AssetManifest.json": "e1f221ea811c35aa7832a426f6239007",
"assets/assets/images/logo_chip.png": "c2dcb6d0c15418e11e59912961f7b98d",
"assets/assets/images/logo_hse.png": "4c25555313f4ff28503e8e1b7390e786",
"assets/assets/images/logo_xml.png": "883adcd3823bdc5786364c3cb0c73bfe",
"assets/assets/poppins/Poppins-Regular.ttf": "41e8dead03fb979ecc23b8dfb0fef627",
"assets/FontManifest.json": "9636982910d5c3a3c2e6c200a6fdbf1b",
"assets/fonts/MaterialIcons-Regular.ttf": "56d3ffdef7a25659eab6a68a3fbfaf16",
"assets/LICENSE": "02e4935b4c61144fbb47dbb308f07ef1",
"assets/packages/cupertino_icons/assets/CupertinoIcons.ttf": "115e937bb829a890521f72d2e664b632",
"favicon.png": "3be3b9193c80514149c175b431f646f1",
"icons/Icon-192.png": "ac9a721a12bbc803b44f645561ecb1e1",
"icons/Icon-512.png": "96e752610906ba2a93c65f8abe1645f1",
"index.html": "1005bc3d40c8a24825fbcf671129c381",
"/": "1005bc3d40c8a24825fbcf671129c381",
"main.dart.js": "dedba97ab3f29cb7a6f03fc5234bf069",
"manifest.json": "2246ca10a43a0f21a33329a70ac74e70"
};

self.addEventListener('activate', function (event) {
  event.waitUntil(
    caches.keys().then(function (cacheName) {
      return caches.delete(cacheName);
    }).then(function (_) {
      return caches.open(CACHE_NAME);
    }).then(function (cache) {
      return cache.addAll(Object.keys(RESOURCES));
    })
  );
});

self.addEventListener('fetch', function (event) {
  event.respondWith(
    caches.match(event.request)
      .then(function (response) {
        if (response) {
          return response;
        }
        return fetch(event.request);
      })
  );
});
