TL :  

DEV : Ranto 3113 et 

# Conception base de donnée :

    Modification : Dans la table demandeur, deplacer signature_digital et photo_identite vers la table piece_justificative et stocker dans la table demande_piece_justificative

    Ajout : Ajouter une nouvelle status de demande "Photo terminé"
        => Dossier crée -> Photo terminé -> Scan Terminé -> ... -> Visa approuvé

# Fonctionalités : Export Pdf

    Dans la page pour afficher les details de la demande, ajouter 2 boutons : 

        ## "Apercu piece justificatif" : Mettre dans une meme fichier pdf tous les piece justificatif du demandeur par ordre d'insertion 

            NB : On ne doit pas telecharger le pdf, on cree juste le pdf pour voir l'apercu des pieces justificatif depuis le navigateur

        ## "Lettre de recu du dossier" : Exporter une lettre qui contient les info sur le demandeur, photo d'identité, QR code (pour le status du demande. Meme QR que celui qui s'affiche quand on clique sur le bouton "QR code" dans la liste de demande), reference du demande 

            Une lettre qui dit que la demande du demandeur a ete recu 

            On ne peut pas cliquer sur ce bouton que quand la status de la demande est "scan terminé"

    Dans la page pour afficher les details de la demande, afficher aussi les pieces justificatif (pas seulement les noms mais aussi affiché les pieces justificatif si possible)

    Enlever le bouton modifier dans la page de detail
     
