/* global L */
/* =========================
   MAP MANAGER (UNIQUE)
   ========================= */

const mapsRegistry = new Map();

function afficherCarte({ container, adresse, zoom = 15 }) {
    if (!container || !adresse) return;

    // ✅ Appel via backend proxy
    fetch(`/api/geocode?address=${encodeURIComponent(adresse)}`)
        .then(res => res.json())
        .then(data => {
            if (!data || data.length === 0) {
                container.innerHTML = "Adresse introuvable";
                return;
            }

            const lat = data[0].lat;
            const lon = data[0].lon;

            let mapData = mapsRegistry.get(container);

            if (!mapData) {
                const map = L.map(container).setView([lat, lon], zoom);

                L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
                    attribution: "© OpenStreetMap"
                }).addTo(map);

                const marker = L.marker([lat, lon]).addTo(map);

                mapsRegistry.set(container, { map, marker });

                setTimeout(() => map.invalidateSize(), 200);
            } else {
                mapData.map.setView([lat, lon], zoom);
                mapData.marker.setLatLng([lat, lon]);
            }

            mapsRegistry.get(container).marker
                .bindPopup(adresse)
                .openPopup();
        })
        .catch(() => {
            container.innerHTML = "Erreur de chargement de la carte";
        });
}

/* =========================
   MAPS STATIQUES
   ========================= */
document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".map").forEach(div => {
        afficherCarte({
            container: div,
            adresse: div.dataset.address
        });
    });
});

/* =========================
   PREVIEW ADRESSE
   ========================= */
document.getElementById("lieu")?.addEventListener("blur", e => {
    afficherCarte({
        container: document.getElementById("map-preview"),
        adresse: e.target.value
    });
});

/* =========================
   MAP CRENEAU
   ========================= */
function afficherMapCreneau(adresse) {
    afficherCarte({
        container: document.getElementById("map-creneau"),
        adresse
    });
}

/* =========================
   GOOGLE MAPS
   ========================= */
function openGoogleMaps(address) {
    if (!address) return;

    const url = `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(address)}`;
    window.open(url, "_blank");
}

function openGoogleMapsFromCurrentLocation(destinationAddress) {
    if (!destinationAddress) {
        alert("Adresse non renseignée");
        return;
    }

    if (!navigator.geolocation) {
        alert("La géolocalisation n'est pas supportée par votre navigateur.");
        return;
    }


    navigator.geolocation.getCurrentPosition(
        position => {
            const lat = position.coords.latitude;
            const lon = position.coords.longitude;

            // Uses the standard "Directions" API
            const url = `https://www.google.com/maps/dir/?api=1&origin=${lat},${lon}&destination=${encodeURIComponent(destinationAddress)}`;

            window.open(url, "_blank");
        },
        () => {
            alert("Impossible de récupérer votre position.");
        }
    );
}
