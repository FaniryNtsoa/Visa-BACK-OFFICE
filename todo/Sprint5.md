TL :  

DEV : Ranto 3113 et 

# Conception base de donnée : 

    Modifier la table demandeur pour ajouter les champs (photo d'identité et signature digital)

# Fonctionanités : Utilisation du track pad et du caméra de l'ordinateur

    Dans la liste des demandes il y a un bouton details qui redirige vers une page pour afficher les details de la demande dont les details à propos du demandeur. 

        . Ajouter dans cette page de detail la photo et la signature du demandeur 
    
        . Dans cette page ajouter un bouton "photo et signature" pour prendre une photo et faire une signature 
    
            ## Quand on clique sur ce bouton, on est rediriger vers une page pour prendre une photo d'identité à partir du camera frontal de l'ordinateur et faire une signature (on fait la signature avec le track pad)

            ## Le Bouton sert à ajouter ou à modifier la photo ou la signature ou les deux 

            ## Ajout d'une nouvelle regle de gestion : 
                
                . avec les regles de gestion existant, on ajoute que si la photo ou la signature ou les deux sont manquante, le status de la demande ne peut pas passer en "scan terminer"

                . Si le status de la demande est autre que "dossier cree" le bouton "photo et signature" devient disable