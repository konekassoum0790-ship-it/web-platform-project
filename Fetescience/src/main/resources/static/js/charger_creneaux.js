/* * Updates the content of the info box based on the current selection.
 */
function updateInfoBox(type) {
    const select = document.getElementById(type === 'atelier' ? 'atelier' : 'creneau');

    if (!select || select.selectedIndex <= 0) return;

    const selectedOption = select.options[select.selectedIndex];

    if (type === 'atelier') {
        document.getElementById('info-atelier-titre').innerText = selectedOption.dataset.titre || "";
        document.getElementById('info-atelier-animateur').innerText = selectedOption.dataset.animateur || "";

        const desc = selectedOption.dataset.description;
        document.getElementById('info-atelier-description').innerText = desc ? desc : "Aucune description disponible.";

    } else if (type === 'creneau') {
        document.getElementById('info-creneau-horaire').innerText = selectedOption.dataset.horaire || "";
        document.getElementById('info-creneau-duree').innerText = selectedOption.dataset.duree || "";
        document.getElementById('info-creneau-lieu').innerText = selectedOption.dataset.lieu || "";
    }
}

/* * Called when the ATELIER selection changes
 */
function chargerCreneaux() {
    const atelierSelect = document.getElementById("atelier");
    const atelierId = atelierSelect ? atelierSelect.value : null;

    const creneauSelect = document.getElementById("creneau");
    const creneauGroup = document.getElementById("creneau-group");
    const submitBtn = document.getElementById("btn-submit"); // ✅ Get the button

    // 1. Reset Creneau List
    creneauSelect.innerHTML = '<option value="" disabled selected>-- Chargement... --</option>';
    creneauSelect.disabled = true;

    // 2. ✅ FIX: Disable button immediately when atelier changes
    if (submitBtn) submitBtn.disabled = true;

    // 3. Hide Creneau Details
    const creneauDetails = document.getElementById("details-creneau");
    if (creneauDetails) creneauDetails.style.display = 'none';

    // 4. Update Atelier Details if open
    const atelierDetails = document.getElementById("details-atelier");
    if (atelierDetails && atelierDetails.style.display === 'block') {
        updateInfoBox('atelier');
    }

    if (!atelierId) {
        if (creneauGroup) creneauGroup.style.display = 'none';
        return;
    }

    // 5. Fetch new Creneaux
    fetch(`/api/ateliers/${atelierId}/creneaux`)
        .then(response => {
            if (!response.ok) throw new Error("Erreur réseau");
            return response.json();
        })
        .then(data => {
            creneauSelect.innerHTML = '<option value="" disabled selected>-- Sélectionnez un créneau --</option>';

            if (data.length === 0) {
                const option = document.createElement("option");
                option.text = "Aucun créneau disponible";
                option.disabled = true;
                creneauSelect.add(option);
            } else {
                data.forEach(creneau => {
                    const option = document.createElement("option");
                    option.value = creneau.id;
                    option.text = `${creneau.horaireDebut}h00 - ${creneau.lieu}`;

                    // Dataset
                    option.dataset.horaire = creneau.horaireDebut + "h00";
                    option.dataset.duree = creneau.duree + " min";
                    option.dataset.lieu = creneau.lieu;

                    const places = creneau.placesRestantes !== undefined ? creneau.placesRestantes : 0;

                    if (places <= 0) {
                        option.disabled = true;
                        option.text += " [COMPLET]";
                        option.style.color = "#dc3545";
                    } else {
                        option.text += ` (Places: ${places})`;
                    }

                    creneauSelect.add(option);
                });
                creneauSelect.disabled = false;
            }

            if (creneauGroup) creneauGroup.style.display = 'block';
        })
        .catch(error => {
            console.error("Erreur:", error);
            creneauSelect.innerHTML = '<option value="" disabled selected>-- Erreur de chargement --</option>';
            if (creneauGroup) creneauGroup.style.display = 'block';
        });
}

/* * Called when the CRENEAU selection changes
 */
function onCreneauChange() {
    const creneauSelect = document.getElementById("creneau");
    const submitBtn = document.getElementById("btn-submit");
    const box = document.getElementById("details-creneau");

    if (submitBtn) {
        submitBtn.disabled = !creneauSelect.value;
    }

    if (!creneauSelect.value) return;

    const selectedOption = creneauSelect.options[creneauSelect.selectedIndex];
    const lieu = selectedOption.dataset.lieu;

    // 🔥 AFFICHER LES DÉTAILS
    box.style.display = "block";
    updateInfoBox('creneau');

    // 🔥 AFFICHER LA MAP
    afficherMapCreneau(lieu);
}

/* * Handles clicking the "Eye" icon
 */
function toggleDetails(type) {
    const select = document.getElementById(type === 'atelier' ? 'atelier' : 'creneau');
    const box = document.getElementById("details-" + type);

    if (select.selectedIndex <= 0) {
        alert("Veuillez d'abord sélectionner une option.");
        return;
    }

    if (box.style.display === 'block') {
        box.style.display = 'none';
    } else {
        updateInfoBox(type);
        box.style.display = 'block';
    }
}