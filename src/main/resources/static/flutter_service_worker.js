'use strict';
const CACHE_NAME = 'flutter-app-cache';
const RESOURCES = {
  "assets/AssetManifest.json": "17679447ef98020b656bfe346379b611",
"assets/assets/Bebas_Neue/BebasNeue-Regular.ttf": "21bb70b62317f276f2e97a919ff5bd8c",
"assets/assets/images/logo_chip.png": "94a9d3d3e41f2db662e7e2e64184967f",
"assets/assets/images/logo_hse.png": "d6c2bf0efff14c25369a3cad61798429",
"assets/assets/images/logo_xml.png": "c7659af8ecd90af62a06cf0108e691f8",
"assets/assets/Roboto/Roboto-Regular.ttf": "11eabca2251325cfc5589c9c6fb57b46",
"assets/FontManifest.json": "cfca5238b8580b2e2c8d9d8f9a16beab",
"assets/fonts/MaterialIcons-Regular.ttf": "56d3ffdef7a25659eab6a68a3fbfaf16",
"assets/LICENSE": "02e4935b4c61144fbb47dbb308f07ef1",
"assets/packages/cupertino_icons/assets/CupertinoIcons.ttf": "115e937bb829a890521f72d2e664b632",
"favicon.png": "f8d38b67fea69931984401294054a797",
"icons/Icon-192.png": "ac9a721a12bbc803b44f645561ecb1e1",
"icons/Icon-512.png": "96e752610906ba2a93c65f8abe1645f1",
"index.html": "1005bc3d40c8a24825fbcf671129c381",
"/": "1005bc3d40c8a24825fbcf671129c381",
"main.dart.js": "4a5e0a5b84a7df12acf427d69327be8d",
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
