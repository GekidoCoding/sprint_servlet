package mg.sprint.framework.page;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class Error {
    public void displayErrorPage(HttpServletResponse resp, Exception e) throws IOException {
        resp.setContentType("text/html; charset=UTF-8");
        resp.getWriter().println("  <!DOCTYPE html>\n" + //
                "    <html lang=\"fr\">\n" + //
                "    <head>\n" + //
                "        <meta charset=\"UTF-8\">\n" + //
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" + //
                "        <title>Système Temporairement Indisponible</title>\n" + //
                "        <link rel=\"preconnect\" href=\"https://fonts.googleapis.com\">\n" + //
                "        <link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin>\n" + //
                "        <link href=\"https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;700;800&display=swap\" rel=\"stylesheet\">\n" + //
                "        <style>\n" + //
                "            :root {\n" + //
                "                --primary-gradient: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);\n" + //
                "                --secondary-gradient: linear-gradient(45deg, #ff6b6b, #4ecdc4, #45b7d1, #96ceb4, #ffeaa7);\n" + //
                "                --dark-gradient: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);\n" + //
                "                --glass-bg: rgba(255, 255, 255, 0.1);\n" + //
                "                --glass-border: rgba(255, 255, 255, 0.2);\n" + //
                "                --text-primary: #ffffff;\n" + //
                "                --text-secondary: rgba(255, 255, 255, 0.8);\n" + //
                "                --error-color: #ff6b6b;\n" + //
                "                --success-color: #51cf66;\n" + //
                "                --warning-color: #ffd43b;\n" + //
                "            }\n" + //
                "            \n" + //
                "            * {\n" + //
                "                margin: 0;\n" + //
                "                padding: 0;\n" + //
                "                box-sizing: border-box;\n" + //
                "            }\n" + //
                "            \n" + //
                "            body {\n" + //
                "                font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;\n" + //
                "                background: var(--dark-gradient);\n" + //
                "                min-height: 100vh;\n" + //
                "                display: flex;\n" + //
                "                align-items: center;\n" + //
                "                justify-content: center;\n" + //
                "                overflow: hidden;\n" + //
                "                position: relative;\n" + //
                "            }\n" + //
                "            \n" + //
                "            /* Arrière-plan animé avec particules */\n" + //
                "            .animated-bg {\n" + //
                "                position: fixed;\n" + //
                "                top: 0;\n" + //
                "                left: 0;\n" + //
                "                width: 100%;\n" + //
                "                height: 100%;\n" + //
                "                z-index: -2;\n" + //
                "                background: var(--dark-gradient);\n" + //
                "            }\n" + //
                "            \n" + //
                "            .particle {\n" + //
                "                position: absolute;\n" + //
                "                background: rgba(255, 255, 255, 0.1);\n" + //
                "                border-radius: 50%;\n" + //
                "                animation: float-particles 15s infinite linear;\n" + //
                "            }\n" + //
                "            \n" + //
                "            .particle:nth-child(1) { width: 4px; height: 4px; left: 10%; animation-delay: 0s; }\n" + //
                "            .particle:nth-child(2) { width: 6px; height: 6px; left: 20%; animation-delay: 2s; }\n" + //
                "            .particle:nth-child(3) { width: 3px; height: 3px; left: 30%; animation-delay: 4s; }\n" + //
                "            .particle:nth-child(4) { width: 5px; height: 5px; left: 40%; animation-delay: 6s; }\n" + //
                "            .particle:nth-child(5) { width: 4px; height: 4px; left: 50%; animation-delay: 8s; }\n" + //
                "            .particle:nth-child(6) { width: 6px; height: 6px; left: 60%; animation-delay: 10s; }\n" + //
                "            .particle:nth-child(7) { width: 3px; height: 3px; left: 70%; animation-delay: 12s; }\n" + //
                "            .particle:nth-child(8) { width: 5px; height: 5px; left: 80%; animation-delay: 14s; }\n" + //
                "            .particle:nth-child(9) { width: 4px; height: 4px; left: 90%; animation-delay: 16s; }\n" + //
                "            \n" + //
                "            @keyframes float-particles {\n" + //
                "                0% { transform: translateY(100vh) rotate(0deg); opacity: 0; }\n" + //
                "                10% { opacity: 1; }\n" + //
                "                90% { opacity: 1; }\n" + //
                "                100% { transform: translateY(-100vh) rotate(360deg); opacity: 0; }\n" + //
                "            }\n" + //
                "            \n" + //
                "            /* Orbes lumineux flottants */\n" + //
                "            .orb {\n" + //
                "                position: absolute;\n" + //
                "                border-radius: 50%;\n" + //
                "                filter: blur(40px);\n" + //
                "                animation: float-orbs 8s ease-in-out infinite;\n" + //
                "                opacity: 0.3;\n" + //
                "            }\n" + //
                "            \n" + //
                "            .orb1 {\n" + //
                "                width: 200px;\n" + //
                "                height: 200px;\n" + //
                "                background: linear-gradient(45deg, #ff6b6b, #4ecdc4);\n" + //
                "                top: 10%;\n" + //
                "                left: 10%;\n" + //
                "                animation-delay: 0s;\n" + //
                "            }\n" + //
                "            \n" + //
                "            .orb2 {\n" + //
                "                width: 150px;\n" + //
                "                height: 150px;\n" + //
                "                background: linear-gradient(45deg, #667eea, #764ba2);\n" + //
                "                bottom: 20%;\n" + //
                "                right: 15%;\n" + //
                "                animation-delay: 2s;\n" + //
                "            }\n" + //
                "            \n" + //
                "            .orb3 {\n" + //
                "                width: 100px;\n" + //
                "                height: 100px;\n" + //
                "                background: linear-gradient(45deg, #f093fb, #f5576c);\n" + //
                "                top: 50%;\n" + //
                "                right: 10%;\n" + //
                "                animation-delay: 4s;\n" + //
                "            }\n" + //
                "            \n" + //
                "            @keyframes float-orbs {\n" + //
                "                0%, 100% { transform: translate(0, 0) scale(1); }\n" + //
                "                33% { transform: translate(30px, -30px) scale(1.1); }\n" + //
                "                66% { transform: translate(-20px, 20px) scale(0.9); }\n" + //
                "            }\n" + //
                "            \n" + //
                "            /* Conteneur principal */\n" + //
                "            .error-container {\n" + //
                "                background: var(--glass-bg);\n" + //
                "                backdrop-filter: blur(20px);\n" + //
                "                border: 1px solid var(--glass-border);\n" + //
                "                border-radius: 24px;\n" + //
                "                padding: 3rem;\n" + //
                "                max-width: 800px;\n" + //
                "                width: 90%;\n" + //
                "                text-align: center;\n" + //
                "                position: relative;\n" + //
                "                box-shadow: \n" + //
                "                    0 25px 50px rgba(0, 0, 0, 0.3),\n" + //
                "                    inset 0 1px 0 rgba(255, 255, 255, 0.2);\n" + //
                "                animation: slideIn 1s ease-out;\n" + //
                "                z-index: 10;\n" + //
                "            }\n" + //
                "            \n" + //
                "            @keyframes slideIn {\n" + //
                "                from { \n" + //
                "                    opacity: 0; \n" + //
                "                    transform: translateY(-50px) scale(0.9); \n" + //
                "                }\n" + //
                "                to { \n" + //
                "                    opacity: 1; \n" + //
                "                    transform: translateY(0) scale(1); \n" + //
                "                }\n" + //
                "            }\n" + //
                "            \n" + //
                "            /* Animation de l'icône d'erreur */\n" + //
                "            .error-icon {\n" + //
                "                width: 120px;\n" + //
                "                height: 120px;\n" + //
                "                margin: 0 auto 2rem;\n" + //
                "                position: relative;\n" + //
                "                animation: pulse-error 2s ease-in-out infinite;\n" + //
                "            }\n" + //
                "            \n" + //
                "            .error-circle {\n" + //
                "                width: 100%;\n" + //
                "                height: 100%;\n" + //
                "                border: 4px solid var(--error-color);\n" + //
                "                border-radius: 50%;\n" + //
                "                position: relative;\n" + //
                "                background: linear-gradient(135deg, rgba(255, 107, 107, 0.2), rgba(255, 107, 107, 0.1));\n" + //
                "                display: flex;\n" + //
                "                align-items: center;\n" + //
                "                justify-content: center;\n" + //
                "            }\n" + //
                "            \n" + //
                "            .error-x {\n" + //
                "                position: relative;\n" + //
                "                width: 40px;\n" + //
                "                height: 40px;\n" + //
                "            }\n" + //
                "            \n" + //
                "            .error-x::before,\n" + //
                "            .error-x::after {\n" + //
                "                content: '';\n" + //
                "                position: absolute;\n" + //
                "                width: 100%;\n" + //
                "                height: 4px;\n" + //
                "                background: var(--error-color);\n" + //
                "                border-radius: 2px;\n" + //
                "                top: 50%;\n" + //
                "                left: 0;\n" + //
                "            }\n" + //
                "            \n" + //
                "            .error-x::before {\n" + //
                "                transform: translateY(-50%) rotate(45deg);\n" + //
                "            }\n" + //
                "            \n" + //
                "            .error-x::after {\n" + //
                "                transform: translateY(-50%) rotate(-45deg);\n" + //
                "            }\n" + //
                "            \n" + //
                "            @keyframes pulse-error {\n" + //
                "                0%, 100% { transform: scale(1); }\n" + //
                "                50% { transform: scale(1.05); }\n" + //
                "            }\n" + //
                "            \n" + //
                "            /* Typographie */\n" + //
                "            .error-code {\n" + //
                "                font-size: 6rem;\n" + //
                "                font-weight: 800;\n" + //
                "                background: var(--secondary-gradient);\n" + //
                "                background-size: 200% 200%;\n" + //
                "                -webkit-background-clip: text;\n" + //
                "                -webkit-text-fill-color: transparent;\n" + //
                "                background-clip: text;\n" + //
                "                animation: gradient-shift 3s ease-in-out infinite;\n" + //
                "                margin-bottom: 1rem;\n" + //
                "                line-height: 1;\n" + //
                "            }\n" + //
                "            \n" + //
                "            @keyframes gradient-shift {\n" + //
                "                0%, 100% { background-position: 0% 50%; }\n" + //
                "                50% { background-position: 100% 50%; }\n" + //
                "            }\n" + //
                "            \n" + //
                "            .error-title {\n" + //
                "                font-size: 2.5rem;\n" + //
                "                font-weight: 700;\n" + //
                "                color: var(--text-primary);\n" + //
                "                margin-bottom: 1rem;\n" + //
                "                text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);\n" + //
                "            }\n" + //
                "            \n" + //
                "            .error-message {\n" + //
                "                font-size: 1.2rem;\n" + //
                "                color: var(--text-secondary);\n" + //
                "                margin-bottom: 2rem;\n" + //
                "                line-height: 1.6;\n" + //
                "                font-weight: 300;\n" + //
                "            }\n" + //
                "            \n" + //
                "            /* Boutons d'action */\n" + //
                "            .action-buttons {\n" + //
                "                display: flex;\n" + //
                "                gap: 1rem;\n" + //
                "                justify-content: center;\n" + //
                "                margin-top: 2rem;\n" + //
                "                flex-wrap: wrap;\n" + //
                "            }\n" + //
                "            \n" + //
                "            .btn {\n" + //
                "                padding: 12px 24px;\n" + //
                "                border: none;\n" + //
                "                border-radius: 12px;\n" + //
                "                font-weight: 600;\n" + //
                "                font-size: 1rem;\n" + //
                "                cursor: pointer;\n" + //
                "                transition: all 0.3s ease;\n" + //
                "                position: relative;\n" + //
                "                overflow: hidden;\n" + //
                "                text-decoration: none;\n" + //
                "                display: inline-flex;\n" + //
                "                align-items: center;\n" + //
                "                gap: 8px;\n" + //
                "            }\n" + //
                "            \n" + //
                "            .btn-primary {\n" + //
                "                background: var(--primary-gradient);\n" + //
                "                color: white;\n" + //
                "                box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);\n" + //
                "            }\n" + //
                "            \n" + //
                "            .btn-secondary {\n" + //
                "                background: rgba(255, 255, 255, 0.1);\n" + //
                "                color: var(--text-primary);\n" + //
                "                border: 1px solid rgba(255, 255, 255, 0.2);\n" + //
                "            }\n" + //
                "            \n" + //
                "            .btn:hover {\n" + //
                "                transform: translateY(-2px);\n" + //
                "                box-shadow: 0 12px 30px rgba(0, 0, 0, 0.4);\n" + //
                "            }\n" + //
                "            \n" + //
                "            .btn::before {\n" + //
                "                content: '';\n" + //
                "                position: absolute;\n" + //
                "                top: 0;\n" + //
                "                left: -100%;\n" + //
                "                width: 100%;\n" + //
                "                height: 100%;\n" + //
                "                background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);\n" + //
                "                transition: left 0.5s;\n" + //
                "            }\n" + //
                "            \n" + //
                "            .btn:hover::before {\n" + //
                "                left: 100%;\n" + //
                "            }\n" + //
                "            \n" + //
                "            /* Responsive Design */\n" + //
                "            @media (max-width: 768px) {\n" + //
                "                .error-container {\n" + //
                "                    padding: 2rem;\n" + //
                "                    margin: 1rem;\n" + //
                "                }\n" + //
                "                \n" + //
                "                .error-code {\n" + //
                "                    font-size: 4rem;\n" + //
                "                }\n" + //
                "                \n" + //
                "                .error-title {\n" + //
                "                    font-size: 2rem;\n" + //
                "                }\n" + //
                "                \n" + //
                "                .error-message {\n" + //
                "                    font-size: 1rem;\n" + //
                "                }\n" + //
                "                \n" + //
                "                .action-buttons {\n" + //
                "                    flex-direction: column;\n" + //
                "                    align-items: center;\n" + //
                "                }\n" + //
                "                \n" + //
                "                .btn {\n" + //
                "                    width: 100%;\n" + //
                "                    max-width: 250px;\n" + //
                "                }\n" + //
                "            }\n" + //
                "            \n" + //
                "            @media (max-width: 480px) {\n" + //
                "                .error-container {\n" + //
                "                    padding: 1.5rem;\n" + //
                "                }\n" + //
                "                \n" + //
                "                .error-code {\n" + //
                "                    font-size: 3rem;\n" + //
                "                }\n" + //
                "                \n" + //
                "                .error-title {\n" + //
                "                    font-size: 1.5rem;\n" + //
                "                }\n" + //
                "                \n" + //
                "                .error-icon {\n" + //
                "                    width: 80px;\n" + //
                "                    height: 80px;\n" + //
                "                }\n" + //
                "            }\n" + //
                "        </style>\n" + //
                "    </head>\n" + //
                "    <body>\n" + //
                "        <!-- Arrière-plan animé -->\n" + //
                "        <div class=\"animated-bg\">\n" + //
                "            <div class=\"particle\"></div>\n" + //
                "            <div class=\"particle\"></div>\n" + //
                "            <div class=\"particle\"></div>\n" + //
                "            <div class=\"particle\"></div>\n" + //
                "            <div class=\"particle\"></div>\n" + //
                "            <div class=\"particle\"></div>\n" + //
                "            <div class=\"particle\"></div>\n" + //
                "            <div class=\"particle\"></div>\n" + //
                "            <div class=\"particle\"></div>\n" + //
                "        </div>\n" + //
                "        \n" + //
                "        <!-- Orbes lumineux -->\n" + //
                "        <div class=\"orb orb1\"></div>\n" + //
                "        <div class=\"orb orb2\"></div>\n" + //
                "        <div class=\"orb orb3\"></div>\n" + //
                "        \n" + //
                "        <!-- Conteneur principal -->\n" + //
                "        <div class=\"error-container\">\n" + //
                "            <!-- Icône d'erreur animée -->\n" + //
                "            <div class=\"error-icon\">\n" + //
                "                <div class=\"error-circle\">\n" + //
                "                    <div class=\"error-x\"></div>\n" + //
                "                </div>\n" + //
                "            </div>\n" + //
                "            \n" + //
                "            <!-- Code d'erreur avec gradient animé -->\n" + //
                "            <div class=\"error-code\">ERROR</div>\n" + //
                "            \n" + //
                "            <!-- Titre et message d'erreur -->\n" + //
                "            <h1 class=\"error-title\">Oops! Quelque chose s'est mal passé</h1>\n" + //
                "            <p class=\"error-message\">" + (e != null && e.getMessage() != null ? e.getMessage() : "Aucun message d'erreur disponible") + "</p>\n" + //
                "            \n" + //
                "            <!-- Boutons d'action -->\n" + //
                "            <div class=\"action-buttons\">\n" + //
                "                <button class=\"btn btn-primary\" onclick=\"window.location.reload()\">\n" + //
                "                    🔄 Réessayer\n" + //
                "                </button>\n" + //
                "                <button class=\"btn btn-secondary\" onclick=\"window.history.back()\">\n" + //
                "                    ← Retour\n" + //
                "                </button>\n" + //
                "            </div>\n" + //
                "        </div>\n" + //
                "        \n" + //
                "        <script>\n" + //
                "            // Ajouter des particules dynamiquement\n" + //
                "            function createParticle() {\n" + //
                "                const particle = document.createElement('div');\n" + //
                "                particle.className = 'particle';\n" + //
                "                particle.style.left = Math.random() * 100 + '%';\n" + //
                "                particle.style.width = particle.style.height = (Math.random() * 4 + 2) + 'px';\n" + //
                "                particle.style.animationDuration = (Math.random() * 10 + 10) + 's';\n" + //
                "                particle.style.animationDelay = Math.random() * 2 + 's';\n" + //
                "                \n" + //
                "                document.querySelector('.animated-bg').appendChild(particle);\n" + //
                "                \n" + //
                "                // Supprimer la particule après l'animation\n" + //
                "                setTimeout(() => {\n" + //
                "                    particle.remove();\n" + //
                "                }, 20000);\n" + //
                "            }\n" + //
                "            \n" + //
                "            // Créer des particules périodiquement\n" + //
                "            setInterval(createParticle, 3000);\n" + //
                "            \n" + //
                "            // Animation au scroll (si contenu défile)\n" + //
                "            let ticking = false;\n" + //
                "            function updateScrollEffect() {\n" + //
                "                const scrolled = window.pageYOffset;\n" + //
                "                const rate = scrolled * -0.5;\n" + //
                "                document.querySelector('.animated-bg').style.transform = `translateY(${rate}px)`;\n" + //
                "                ticking = false;\n" + //
                "            }\n" + //
                "            \n" + //
                "            window.addEventListener('scroll', function() {\n" + //
                "                if (!ticking) {\n" + //
                "                    requestAnimationFrame(updateScrollEffect);\n" + //
                "                    ticking = true;\n" + //
                "                }\n" + //
                "            });\n" + //
                "        </script>\n" + //
                "    </body>\n" + //
                "    </html>");
    }

    @SuppressWarnings("unused")
    private String formatExceptionForDisplay(Exception e) {
        if (e == null) return "Exception inconnue - Aucun détail disponible";
        
        return e.getMessage() != null ? e.getMessage() : "Aucun message";
    }
}