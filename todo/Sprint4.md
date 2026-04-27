TL : Ranto 3113 

DEV : Faniry 3149 et Dylan 3175

# Base de données : 

    Ajouter une nouvelle attribut dans la table demande (numero demande)

# Techno : 

    Front : Angular ou React

# Fonctionalités :

    I - liste de demande (angular ou react):
 
        Formulaire pour inserer le numero de passeport ou numero demande

                . Apres validation, on verifie si le numero inserer est demande ou passeport 
                    - Si passeport, on est redirigé vers une page qui affiche tous les demandes liés au passeport (par ordre du plus recent)

                    - Si demande, on est redirigé vers une page qui affiche aussi les demandes mais qui affiche en premier lieu la demande qu'on a inserer dans le formulaire puis les demandes lié au passeport du demande du demande qu'on a inseré dans le formulaire 

                    Un bouton pour afficher le details de chaque demande  

     II - QR code : 

        Qu'on vient de creer une nouvelle demande, au lieu de directement etre rediriger dans la liste des demande, on est rediriger dans un page qui nous donne un QR code et c'est apres qu'on est redirigé dans la liste des demande

        Contenu du QR code : information sur le status de la demande (Angular ou React)

        exemple : 25 avril 2026 : dossier creer -> 31 fevrier 2027 : scan terminer -> 32 decembre : visa approuvé            