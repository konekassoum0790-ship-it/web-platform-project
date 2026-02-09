function deleteInscription(id) {
    if (!confirm("Êtes-vous sûr de vouloir vous désinscrire ?")) return;

    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    fetch('/inscriptions/' + id, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        }
    })
        .then(response => {
            if (response.ok) {
                const element = document.getElementById('inscription-row-' + id);
                if (element) {
                    // 1. Add transition for smooth exit
                    element.style.transition = "all 0.5s ease";
                    element.style.opacity = "0";             // Fade out
                    element.style.transform = "translateX(50px)"; // Slide slightly right

                    // 2. Wait for animation (500ms) before removing from HTML
                    setTimeout(() => {
                        element.remove();

                        alert("Désinscription réussie !"); //JS message box

                        // Check if list is empty to reload page (optional)
                        const remainingRows = document.querySelectorAll('.inscription');
                        if (remainingRows.length === 0) {
                            location.reload();
                        }
                    }, 500); // Must match the transition time (0.5s = 500ms)
                }
            } else {
                alert("Erreur lors de la désinscription. Veuillez réessayer.");
            }
        })
        .catch(error => console.error('Erreur:', error));
}