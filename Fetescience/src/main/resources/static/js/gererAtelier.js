

function toggleDescription() {
    var shortDesc = document.getElementById("desc-short");
    var fullDesc = document.getElementById("desc-full");
    var btn = document.getElementById("toggle-desc-btn");

    if (fullDesc.style.display === "none") {
        // Show full
        shortDesc.style.display = "none";
        fullDesc.style.display = "inline";
        btn.innerText = "Voir moins";
    } else {
        // Show short
        shortDesc.style.display = "inline";
        fullDesc.style.display = "none";
        btn.innerText = "Voir plus";
    }
}

function editCreneau(id, horaire, duree, capacite, lieu) {
    // 1. Remplir les champs
    document.getElementById('creneauId').value = id;
    document.getElementById('horaire').value = horaire;
    document.getElementById('duree').value = duree;
    document.getElementById('capacite').value = capacite;
    document.getElementById('lieu').value = lieu;

    // 2. Changer l'interface visuelle
    document.getElementById('form-title').innerText = "✏️ Modifier le créneau";
    document.getElementById('form-title').style.color = "#e63312"; // Secondary color
    document.getElementById('submit-btn').innerText = "Modifier";

    // 3. Afficher le bouton Annuler
    document.getElementById('cancel-btn').style.display = "inline-block";

    // 4. Scroll smooth vers le formulaire (utile sur mobile)
    document.querySelector('.card:last-child').scrollIntoView({behavior: 'smooth'});
}

function resetForm() {
    // 1. Vider le formulaire (reset natif)
    document.getElementById('creneau-form').reset();

    // 2. Vider explicitement l'ID caché pour repasser en mode création
    document.getElementById('creneauId').value = "";

    // 3. Restaurer l'interface visuelle
    document.getElementById('form-title').innerText = "➕ Ajouter un créneau";
    document.getElementById('form-title').style.color = "#0051a5"; // Primary color
    document.getElementById('submit-btn').innerText = "Ajouter ce créneau";

    // 4. Cacher le bouton Annuler
    document.getElementById('cancel-btn').style.display = "none";
}