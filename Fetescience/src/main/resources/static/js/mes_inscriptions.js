// ========================================
// CONFIGURATION
// ========================================

const URL_DESINSCRIPTION = "/inscriptions/desinscrire/";

// ========================================
// 1. THE API CALL
// ========================================
async function desinscriptionAtelier(idInscription) {
    try {
        const url = URL_DESINSCRIPTION + idInscription;

        // Get Tokens for Security
        const csrfTokenMeta = document.querySelector('meta[name="_csrf"]');
        const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');

        // Safety check if meta tags are missing
        if (!csrfTokenMeta || !csrfHeaderMeta) {
            console.error("Erreur Sécurité: Token CSRF introuvable dans le HTML.");
            return false;
        }

        const csrfToken = csrfTokenMeta.getAttribute('content');
        const csrfHeader = csrfHeaderMeta.getAttribute('content');

        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            }
        });

        return response.status === 200;
    } catch (error) {
        console.error('Erreur lors de la désinscription:', error);
        return false;
    }
}

// ========================================
// 2. THE EVENT HANDLER
// ========================================
async function handleDesinscription(event) {
    // Stop any default button behavior (like submitting a form)
    event.preventDefault();

    const bouton = event.target;

    // Find the parent container (the specific row in the list)
    const inscriptionDiv = bouton.closest('.inscription');
    const idInscription = inscriptionDiv.getAttribute('id-inscription');

    if (!idInscription) {
        alert("Erreur: ID introuvable.");
        return;
    }

    if (!confirm("Voulez-vous vraiment vous désinscrire ?")) {
        return;
    }

    // Disable button to prevent double-clicks
    bouton.disabled = true;
    bouton.textContent = "...";

    // Call the API
    const success = await desinscriptionAtelier(idInscription);

    if (success) {
        // ✅ UX: Remove the element from the screen smoothly
        inscriptionDiv.style.transition = "opacity 0.5s";
        inscriptionDiv.style.opacity = "0";

        setTimeout(() => {
            inscriptionDiv.remove();

            // Optional: Reload if you prefer a fresh state
            // window.location.reload(); 
        }, 500);
    } else {
        alert("Erreur technique lors de la désinscription.");
        bouton.disabled = false; // Re-enable if it failed
        bouton.textContent = "Se désinscrire";
    }
}

// ========================================
// 3. INITIALIZATION
// ========================================
document.addEventListener('DOMContentLoaded', () => {
    const boutons = document.querySelectorAll('.btn-desinscription');
    boutons.forEach(btn => {
        btn.addEventListener('click', handleDesinscription);
    });
});