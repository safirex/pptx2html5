/*-------------------------
Initialisation du diaporama
-------------------------*/
var diapos = document.getElementsByClassName("diapo");
var valeurLargeur;
var valeurLongueur;

function init() {
	//fonction pour la mise en place du diapo
	adaptNavBar();
	createProgresseBar();
	afficheNumDiapo();
	
	//remplir des constantes pour le plein écran
	var longueur = diapos[0].style.width;
	var largeur = diapos[0].style.height;
    var chiffreLargeur = longueur.substring(0, longueur.indexOf("pt"));
    var chiffreLongueur = largeur.substring(0, largeur.indexOf("pt"));
	valeurLargeur = parseInt(chiffreLargeur);
	valeurLongueur = parseInt(chiffreLongueur);
	
	//ajouter la prise en compte des fleche du clavier pour changer les diapos
	document.addEventListener('keyup', function (e) {
		switch(e.keyCode){
			case 37:
				precedent();
				break;
			case 39:
				suivant();
				break;
			case 27:
				retourALaNormal();
				break;
		}
	});
}

//adapte la taille de la nav bar en fonction de la taille de la diapo (esthetique)
function adaptNavBar(){
	var diapo = diapos[getNumDiapoActive()];
	var navBar = document.querySelector(".nav");

	var largeur = diapo.style.width;
	var chiffreLargeur = largeur.substring(0, largeur.indexOf("pt"));
	var valeur = parseInt(chiffreLargeur) + 4;
	console.log(chiffreLargeur);
	
	navBar.style.width = valeur + "pt";
}

//affiche le numero de la diapo active dans le input de saisie de numéro de diapo
function afficheNumDiapo(){
	var input = document.querySelector("#numDiapo");
	input.value = (getNumDiapoActive()+1) + "";
}

//créer et la progress bar 
function createProgresseBar(){
	var progress = "<progress value=\"0\" max=\"" + (diapos.length-1) +"\"></progress>";
	var navBar = document.querySelector(".nav");
	var html = navBar.innerHTML;
	navBar.innerHTML = html + "\n" + progress;
}

//met a jour la progresse bar
function majProgresseBar(){
	var progress = document.querySelector("progress");
	progress.value = getNumDiapoActive();
}

/*-------------------------
Navigations dans les diapos
-------------------------*/

//recuperation de la diapo active
function getNumDiapoActive() {
	var num;
	for (var i = 0; i < diapos.length; i++) {
		if (diapos[i].className != "diapo none")
			num = i;
	}
	return num;
}

//fait passer la diapo active en "none" et affiche la suivante
function suivant() {
	var numDiapo = getNumDiapoActive();
	if (numDiapo + 1 < diapos.length) {
		var diapoActive = diapos[numDiapo];
		diapoActive.setAttribute("class", "diapo none");
		var diapoFutur = diapos[numDiapo + 1];
		diapoFutur.setAttribute("class", "diapo");
		afficheNumDiapo();
		majProgresseBar();
	}
}

//fait passer la diapo active en "none" et affiche la precedente
function precedent() {
	var numDiapo = getNumDiapoActive();
	if (numDiapo - 1 >= 0) {
		var diapoActive = diapos[numDiapo];
		diapoActive.setAttribute("class", "diapo none");
		var diapoFutur = diapos[numDiapo - 1];
		diapoFutur.setAttribute("class", "diapo");
		afficheNumDiapo();
		majProgresseBar();
	}
}

//change la diapo active par celle du numero recuperer 
function changeDiapo() {
	var input = document.getElementsByTagName("input")[0];
	var numFuturDiapo = parseInt(input.value) - 1;

	var numDiapo = getNumDiapoActive();
	if (numFuturDiapo < diapos.length) {
		var diapoActive = diapos[numDiapo];
		diapoActive.setAttribute("class", "diapo none");
		var diapoFutur = diapos[numFuturDiapo];
		diapoFutur.setAttribute("class", "diapo");
		afficheNumDiapo();
		majProgresseBar();
	}
}

/*----------------
Option plein écran
----------------*/
//met la diapo en plein écran ou l'enlève (résultat pas ouf)
var full = false;
function fullScreen() {
	var elem = document.body;
	
	var imgAll = document.querySelectorAll("img");
	var img = imgAll[imgAll.length-1];
	
	if (full == true) {
    	document.exitFullscreen().then(() => console.log("Document Exited form Full screen mode")).catch((err) => console.error(err));
		img.src = "data:image/png;base64,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"
		full = false;
		retourALaNormal();
  	} else { 
    	elem.requestFullscreen();
		img.src = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAGAAAABgCAYAAADimHc4AAAFX0lEQVR4Xu2dS4gcVRSG/7+YmXZE8BVciCiC4mMh+CAQiLoQfD+jURea+AAfswtuBHUXdCduJMZNdHQTifgKiggBFSGCxheKqCgudCXqSseeGX+5drW2bVfXPfdWT4Xpc7dzzrld31e3XlOcItbBkCTLZpCkJT43VtIuAPeNqrOmPyR3Q6ryD2cBkhYB3F71213ApPYKAJL2Abhx3BQuYAICJHUAvAzg8rryLqCOkPHvko4r4V8Yk+oCYihFxkg6qYR/fmQKXEAsqZo4SWeU8M+0lHQBFloVsZLOLeGfbC3nAqzEhuIlbQbwGoBjEkotuIAEav0USZcBeBXAXEKZ7SQXp1aApILknwng/k6RdDOAvYn5W0mGewQ/CacAlLQHwB0JuV0A15N8o587tSsgAV5/z38fwMaE/F8A3EDy7cFcF2AgKelDAOcZUvqhP5R7/gfDuS4gkqakTwCcExk+GPZVued/MSrXBUQQlfQ5gLMjQodDPi73/O+rcl1ADVVJYQ8+PQH+eyX8n8bluoAxdCR9C+DUBPgfAdhM8re6XBdQQUhSOGyYHy0A+JLkWXXg/TJ0/J4frlpOjIXYj5P0WVEUphO1r4ABypKOBPAdgBMS4B8qiiL6MbSvgCHCkjaEwweA4xPgHyyKYpM1L8T7Cug91zkFQLjOP9oKUdK7RVFcZM3zFVASkBSu78PjhaOsECUdKIriEmveYPxUrwBJFwB4B8C8FaKkN4uiqP2ne13dqRUg6WIAbwGYrYM0/PfV1dX9MzMz11jzRsVPpQAAVwLYD6CwQlxZWXlpdnZ2izWvKn5aBSTxW15e3js3N3drUnJFkguIpNntdp/vdDqVrxhGlvlfmAuIINftdvd0Op27IkLNIS6gBtnS0tLu+fn5kW82m2mPSHABTVDMqOECMuA1keoCmqCYUcMFZMBrItUFNEExo4YLyIDXRKoLaIJiRg0XkAGviVQX0ATFjBouIANeE6kuoAmKGTXWhYCM7W891QW0rMAFuICWCbQ8va8AF9AygZan9xXgAlom0PL0vgJcQMsEWp7eV4ALaJlAy9PT2vCuzd+71t0O12JbXcBaUB4zhwtwAfEE/BAUz2oikS5gIljji7qAeFYTiXQBE8EaX9QFxLOaSKQLmAjW+KLrUkD85tsjJT0LYJs9c3SGCzCQlPQKgGsNKbWhLqAWUS9AUugMmNw/oWoaFxAhQNIhAKGXcuPDBYxBWn60IHQcCV3EJzJcQAVWSaFxdYCf0uIrWpYLGIFKUmjtFZrUmbtMRZMvA13AEDFJpwEI3WDNjY6s8EO8CxigJim06DoA4IgUmCk5LqCkJim0ewnX+TMpIFNzXEDvGv82AOEO19xrZwj8wwB2WmRMvQBJ9wDYbYFWEbtAcpf1hYCpFSDp0rJt+2MNwL+T5DPlHfNh/Q3IBra1tkTUi1mSfgZwbG21+oBbSL7QD/MVENE31AqpwsFq2Uk89Gn7Z1hrT90hyAqoAv7vAK4mGS5Z/zOs9adKgKTQxjf3uc6vAK4geXCUHBdQcQiS9CCA3BPujyX8T6tODS5ghABJCwCerD+fjo34OvTmJPnNuCgXMCBAUugEezeA6zLhh++mXEUyrICxwwWUAiQtAQgfIc4d4T9h4UNl4dhfO1zAvwJMN0QVZF8HsIXkH7XkywAXAIS3owOwlI9RDnLeR3JrLPh+nAvoCcjd+xdJbrfCD/HWudflfYAVwhDop0jenwLfBfSo5ayAx0k+kArfBeQJ2EnykRz4LiBdwEMkH82F7wLSBOwg+UQT8F2AXcC9JJ9uCr4LsAnYRvK5JuG7gHgBN5F8sWn4Xq9H4C8TyWyZY3ay3AAAAABJRU5ErkJggg==";
		full = true;
		creerGrandeDiapo();
  	} 
}

//adapte la taille de la diapo à la taille de l'écran pour le plein écran
function creerGrandeDiapo(){
	//calcul du rapport longeur largeur de la diapo
	var rapport = valeurLongueur*100/valeurLargeur;

	//calcul du %age en plus sur la largeur lors du plein écran
	var largeurEcran = screen.height;
	var largeurArrivee = largeurEcran - 10
	var prcBaseLargeur = (100 * valeurLargeur) / largeurArrivee;
	var rapportLargeur = 100 - prcBaseLargeur;

	//calcul du %age en plus sur la longueur lors du plein écran
	var longueurEcran = screen.width;
	var longueurArrivee = longueurEcran * rapport / 100;
	var prcBaseLongueur = (100 * valeurLongueur) / longueurArrivee;
	var rapportLongueur = rapport - prcBaseLongueur;
	
	for (k=0; k<diapos.length; k++){
		var diapo = diapos[k];
		//adapter la taille de la diapo au plein écran
		diapo.style.width = "100%";
		diapo.style.height = longueurArrivee + "pt";

		//placer la diapo redimensioner au centre de l'écran 
		var reste = longueurEcran - longueurArrivee;
		diapo.style.marginTop = (reste / 4) + "pt";

		var div = diapo.querySelectorAll("div");
		for(i=0; i<div.length; i++){
			if(div[i].className === "parag"){
				var span = div[i].querySelectorAll("span");
				for(j=0; j<span.length; j++){
					//adapter la taille de l'écriture
					var fontSize = span[j].style.fontSize;
					var chiffreFont = parseInt(fontSize.substring(0, fontSize.indexOf("pt")));
					span[j].style.fontSize = chiffreFont+5 + "pt";
				}
			}else{
				//adapter les div qui composent la diapo
				var left = div[i].style.left;
				var chiffreLeft = parseInt(left.substring(0, left.indexOf("pt")));
				var valeurLeft = chiffreLeft + (chiffreLeft * prcBaseLargeur/100);
				div[i].style.left = valeurLeft + "pt";

				var top = div[i].style.top;
				var chiffreTop = parseInt(top.substring(0, top.indexOf("pt")));
				var valeurTop = chiffreTop + (chiffreTop * prcBaseLongueur/100);
				div[i].style.top = valeurTop + "pt";

				var width = div[i].style.width;
				var chiffreWidth = parseInt(width.substring(0, width.indexOf("pt")));
				var valeurWidth = chiffreWidth+ (chiffreWidth * rapportLargeur/100);
				div[i].style.width = valeurWidth + "pt";

				var height = div[i].style.height;
				var chiffreHeight = parseInt(height.substring(0, height.indexOf("pt")));
				var valeurHeight = chiffreHeight+ (chiffreHeight * rapportLongueur/100);
				div[i].style.height = valeurHeight + "pt";
			}
		}

		var img = diapo.querySelectorAll("img");
		for (i=0; i<img.length; i++){
			var left = img[i].style.left;
			var chiffreLeft = parseInt(left.substring(0, left.indexOf("pt")));
			var valeurLeft = chiffreLeft + (chiffreLeft * prcBaseLargeur/100);
			img[i].style.left = valeurLeft + "pt";

			var top = img[i].style.top;
			var chiffreTop = parseInt(top.substring(0, top.indexOf("pt")));
			var valeurTop = chiffreTop + (chiffreTop * prcBaseLongueur/100);
			img[i].style.top = valeurTop + "pt";

			var width = img[i].style.width;
			var chiffreWidth = parseInt(width.substring(0, width.indexOf("pt")));
			var valeurWidth = chiffreWidth+ (chiffreWidth * rapportLargeur/100);
			img[i].style.width = valeurWidth + "pt";

			var height = img[i].style.height;
			var chiffreHeight = parseInt(height.substring(0, height.indexOf("pt")));
			var valeurHeight = chiffreHeight+ (chiffreHeight * rapportLongueur/100);
			img[i].style.height = valeurHeight + "pt";
		}

		var video = diapo.querySelectorAll("video");
		for (i=0; i<video.length; i++){
			var left = video[i].style.left;
			var chiffreLeft = parseInt(left.substring(0, left.indexOf("pt")));
			var valeurLeft = chiffreLeft + (chiffreLeft * prcBaseLargeur/100);
			video[i].style.left = valeurLeft + "pt";

			var top = video[i].style.top;
			var chiffreTop = parseInt(top.substring(0, top.indexOf("pt")));
			var valeurTop = chiffreTop + (chiffreTop * prcBaseLongueur/100);
			video[i].style.top = valeurTop + "pt";

			var width = video[i].width;
			var valeurWidth = width+ (width * rapportLargeur/100);
			video[i].style.width = valeurWidth + "pt";

			var height = video[i].height;
			var valeurHeight = height+ (height * rapportLongueur/100);
			video[i].style.height = valeurHeight + "pt";
		}

		var audio= diapo.querySelectorAll("audio");
		for (i=0; i<audio.length; i++){
			var left = audio[i].style.left;
			var chiffreLeft = parseInt(left.substring(0, left.indexOf("pt")));
			var valeurLeft = chiffreLeft + (chiffreLeft * prcBaseLargeur/100);
			audio[i].style.left = valeurLeft + "pt";

			var top = audio[i].style.top;
			var chiffreTop = parseInt(top.substring(0, top.indexOf("pt")));
			var valeurTop = longueurArrivee - valeurLongueur + chiffreTop;
			audio[i].style.top = valeurTop + "pt";
		}
	}
}

//rend leurs taille d'origine au diapo quand on quitte le plein écran
function retourALaNormal(){
	history.go(0);
}

/*---------------
Gestion des médias
----------------*/

function changerFondEcran(valeur) {
	history.go(0);
}

function lancerAudio() {
	var audio = new Audio("son.mp3");
	audio.play();
}