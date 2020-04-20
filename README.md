# pptx2html5

Le but de ce projet est de créer un outil permettant de transformer un fichier PPTX en une animation HTML5 conservant les media, animations et les liens de celui-ci.

Le résultat produit devra être facilement déplaçable par un non informaticien de manière que celui-ci puisse l’intégrer dans le LMS de son établissement ou sur une page web de son choix.

## Version exécutable

* [jar exécutable](https://unice-my.sharepoint.com/:u:/g/personal/saad-el-din_ahmed_etu_unice_fr/EaAW5WJ6SihAnsDjZ7bBlLEBBYZChAqoBw2rFG2lmqJyMw?e=3WGefh) - nécessite d'un mail universitaire (Unice Sophia Antipolis) pour telecharger le fichier!

## Utiliser le logiciel

Ces instructions en ligne de commande vous permettront d’exécuter le logiciel à partir du jar sur une machine locale.

### Prérequis

Veuillez à installer une version récente du JRE Java pour ne pas avoir des erreurs de compatibilité.

### Exécuter le logiciel

Pour exécuter le logiciel, il suffit de rentrer la ligne de commande suivante :
```
java -Dfile.encoding=UTF-8 -jar PPTX2HTML.jar monpptx.pptx
```
Remplacer ``monpptx.pptx`` avec le chemin vers le fichier pptx à convertir.

A la suite de l’exécution, le résultat en html sera exporté dans un répertoire ``exportHTML`` se trouvant dans le répertoire contenant le jar.

## Auteurs

* Ahmed Saad El Din
* Audouard Florian
* Ben El Bey Yessine
* Chatin Eudes
* Relevat Chiara

Voir aussi [Contributeurs](https://git-iutinfo.unice.fr/rey/pt-s4t-g1-pptx2html5/-/graphs/master) pour une liste complète des contributeurs.

## Licence

Ce projet est licencié sous la licence Apache 2.0 - voir le fichier [LICENSE](LICENSE) pour plus de détails.