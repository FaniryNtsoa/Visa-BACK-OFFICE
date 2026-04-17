TL : Ranto 3113 

DEV : Faniry 3149 et Dylan 3175

# Conception base de donnée 

    ## Misy status 3 ny nouvelle demande de titre (Dossier crée, Scan terminé, Visa approuvé)

    ## Visa investisseur sy travailleur ihany alony no atao

    ## Misy ny information obligatoire pour chaque visa ilaina

    ##Misy status ihany koa ny demande (Nouvelle demande de titre, transfert visa, duplicata)
        transfert visa sans données anterieur sy duplicata sans données anterieur tsy fantatro raha atao status ihany koa

# Fonctionalités : Nouvelle demande 

    ## Nouveau titre (investisseur, travailleur) :
        
        . Le formulaire doit contenir : 
            - etat civil (Nom, prenom, ...)
            - information sur le visa qu'il veut :
                Ohatra oe misy selectioner izy oe travailleur no ilainy de mipoitra eo daoly ny zavatra ilaina @ ilay visa miaraka @ pieces justificatifs ilaina rehetra 
            - pieces justificatifs ialina @ ilay visa ilainy (check box)
            - information momba an'ilay passport any 
            - Numero visa transformable any (Tsy aiko raha ilaina)

            - boutton validé ou enregistrer (refa enregistrer de "dossier crée" ny status an ilay demande)

        . Rehefa vita le enregistrement eo @ ilay formulaire de rediriger makany am liste (na details izany oe page mi-affiche ny information rehetra nosoratana tao @ formulaire)

            NB : Tsy afaka manao enregistrement raha tsy feno izay information obligatoire raha tsy obligatoire dia tsy maninona fa ao @ ilay "Modifier ou ajouter information" no mi-ajouter ny tsy obligatoire raha tsy voafeno tao @ formulaire

        . Eo @ ilay page de liste (na page details) dia misy boutton oe modifier raha page de liste de pour chaque demande eo dia misy boutton oe (modifier ou ajouter information) daoly ary io boutton io miverina any @ ilay formulaire (Raha mety fa afaka atao page afa misty raha ohatra oe sarotra be) FA izay information efa nosoratany dia efa pré-remplis dia afaka modifier-na avy eo raha misy dia ampiana raha misy tokony ampiana 

Context : 

    Tonga androany ilay olona mangataka visa de mitondra dossier fa tsy ampy ary izay dossier oentiny androany alony no fenoina eo am le formulaire fa tonga ndray izy rahampitso mitondra ny dossier ambiny dia ao @ "Modifier ou ajouter information" no fenoina indray le dossier hoentiny 

        . Misy ihany koa boutton oe "Scan ou upload fichier" eo akaikin ilay "Modifier ou Ajouter information" (Sprint 3)
                
        . Misy ihany koa boutton oe "Scan terminer" 

            NB : Tokony manao erreur na chose comme cela raha manindry an'io boutton io nefa tsy uploader-daoly ny piece justificatif ilaina (obligatoire ou non tsy maintsy uploader-na daoly) FA raha efa uploader daoly ka manindry azy dia lasa "Scan teminer" ny status an'ilay demande ARY tsy mahazo manova na manao ajout informantion intsony (Ao @ dossier crée ihany no mahazo manova na manao ajout information)

    ## Transfert Visa (Cas où very ny passport)

Context : 

    Nouveau titre -> Dossier cree -> scan terminer -> ... -> visa approuvé -> mahazo Carte resident sy visa 

        . Contenu du formulaire : 

            - Numero du nouveau passport 
            - numero du visa 
            - boutton enregistrer (Rehefa enregistrer de lasa "dossier cree" HONO fa tsy aiko tsara ny status eo)

        . Ce qui ce passe en arriere plan : 

            - enregistrer ao @ table passport ilay passport vaovao saisi teo @ ilay formulaire 
            - Misy ligne vaovao crée ao @ table Visa sy Carte resident satria lié @ passport ireo 
            - Izay enregistrement farany no Visa, Carte resident, Passport actuel an ilay demandeur

    ## Duplicata (Cas où very ilay carte resident)

        . Mitovy @ transfert visa ihany ny contenu @ ilay formulaire 

        . Mamoaka an ilay carte resident taloha ihany fa marqué oe duplicata (ohatran ilay CIN reny raha efa nahita nareo)

            NB : Tsy aiko oe misy ligne vaovao ve ao @ carte resident (Raha ohatra oe asina dia misy champ 2 oe "bool duplicata default false" ary "duplicata_id_carte_resident default null" raha true ilay "bool duplicata" dia tokony hisy ny id an ilay carte resident supposé very eo) 