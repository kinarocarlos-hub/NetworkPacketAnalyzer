const CACHE = 'netpulse-v2';
const LOCAL = ['/', '/manifest.json', '/icons/icon-192.png', '/icons/icon-512.png'];

self.addEventListener('install', e => {
    e.waitUntil(caches.open(CACHE).then(c => c.addAll(LOCAL)));
    self.skipWaiting();
});

self.addEventListener('activate', e => {
    e.waitUntil(caches.keys().then(keys =>
        Promise.all(keys.filter(k => k !== CACHE).map(k => caches.delete(k)))
    ));
    self.clients.claim();
});

self.addEventListener('fetch', e => {
    const url = new URL(e.request.url);

    // Network-only for API calls
    if (url.pathname.startsWith('/api')) {
        e.respondWith(
            fetch(e.request).catch(() =>
                new Response(JSON.stringify({ error: 'offline', packets: [], totalPackets: 0,
                    tcpPackets: 0, udpPackets: 0, otherPackets: 0, topTalkers: {} }),
                    { headers: { 'Content-Type': 'application/json' } })
            )
        );
        return;
    }

    // Network-only for CDN resources (fonts, tailwind, chartjs, fontawesome)
    if (url.origin !== self.location.origin) {
        e.respondWith(fetch(e.request));
        return;
    }

    // Cache-first for local assets
    e.respondWith(
        caches.match(e.request).then(cached => {
            if (cached) return cached;
            return fetch(e.request).then(res => {
                const clone = res.clone();
                caches.open(CACHE).then(c => c.put(e.request, clone));
                return res;
            });
        })
    );
});
