# StudentCare

Käyttöliittymät 2019, harjoitustyö D

## Käytetty tekniikka

Työssäni käytin työkaluna Vaadin Framework 8:aa. Vaadin valikoitui työkaluksi JavaFX:n sijaan siitä tehdyn selvitystyön motivoimana, ja yleisen mielenkiinnon takia kokeilla jotain uutta.
Vaadin salli myös kielen pitämisen Javassa, jolloin tehtävän alkuperäistä koodia pystyi käyttämään sellaisenaan, eikä sitä tarvinnut muokata (muutamaa poikkeusta lukuunottamatta).

## Toteutetut tehtävänannot

Kaikki kolme käyttäjätyyppiä on toteutettu, mutta graafista tietokannan alustusta ei toteutuksessani ole. Myöskään slowModea ei ole, mutta se olisi jokatapauksessa ollut Vaatimen kanssa turha, sillä oletuksena oleva latauspalkki informoi käyttäjää jokatapauksessa sivun latauksen tilanteesta.
Ohjelmakoodissa selostetaan kommentteina kunkin näkymäluokan tarkoitus, ja koodia ylipäätään.

## Asentaminen ja ajaminen

Toteutuksessani riippuvuuksien hallinta on siirretty sbt:stä Maveniin, sillä sbt:n Vaadin tuki oli vanhentunut ja aikataulun takia hankala itse toteuttaa.

Mavenin lataukset ja asennusohjeet löytyvät [projektin sivuilta](https://maven.apache.org/). 
Projekti ladataan komennolla `git clone https://gitlab.utu.fi/arhutt/studentcare/` , jonka jälkeen tarvittavat osat asennetaan komennolla `mvn install` (projektikansiossa).

Itse ohjelma ajetaan paikallisessa Jetty-palvelimessa, joka käynnistyy komennolla `mvn jetty:run`. Sovellus on valmis käytettäväksi kun terminaalissa lukee "Started Jetty Server". Selaimessa sovellus löytyy osoitteesta [localhost:8080](localhost:8080). Toiminnallisuus testattu Firefox-selaimella. Näkymät eivät skaalaudu ikkunan koon mukaan, mutta kaikki näkyvät oikein kun selainikkuna on tarpeeksi suuri.

Editointi on testattu toimivaksi IntelliJ IDEA:lla, jossa riippuvuudet sa toimimaan kun ohjelma pyytää "Import Maven project". Sbt:tä ei tule hyväksyä. Mikäli IntelliJ:n sulkee ja käynnistää uudelleen, riippuvuudet unohtuu ja ne tulee päivittää Maven-välilehdestä. Jos sekään ei auta, projektin poistaminen ja uudelleenkloonaaminen auttaa.

Tässä muutamat käyttäjät kirjautumiseen:

Kaikki oikeudet: tunnus: kaeese , salasana: kp1234

Opettaja: tunnus: daanad , salasana: dl1234

Opiskelija: tunnus: ilvijo , salasana: il1234

## Ohjelman rakenne

Toteutuksessa on säilytetty alkuperäiset luokat Main ja MainApp kääntymisen sallimiseksi, mutta niitä ei käytetä. Pääluokkana toimii MyUI, joka tarkistaa onko käyttäjä jo kirjautunut sisään. Jos ei ole, luodaan kirjautumissivu joka sitoo kirjautuneen käyttäjän SessionAuthentication luokkaan, johon taas tallennetaan käyttäjän Student olio, ja SqlConnection olio. Tätä oliota kutsutaan aina kun halutaan myöhemmissä vaiheissa viitata käyttäjään tai Sql-yhteyteen.

Onnistuneen kirjautumisen jälkeen avataan runkonäkymä MainView, jossa ylimpänä on ohjelman päävalikko (näkyy kaikissa näkymissä). Päävalikon vaihtoehdot riippuvat käyttäjän oikeuksista (opettaja tai hallintohenkilö), ja ne luodaan MainViewissä.
Runkonäkymässä on alinäkymä viewContainer, johon vaihtuvat näkymät sijoitetaan. Näkymät luodaan Vaatimen Navigator luokana avulla.

Muissa näkymissä käytetään tietojen esittämiseen paljon Vaatimen Grid-taulukkoa, joka uudelleenalustetaan aina näkymään vaihdettaessa käyttäen Vaatimen enter() metodia. Samalla metodilla uudelleenalustetaan myös muitakin kuin taulukoita nollaamalla näkymän komponentit. Mikäli käyttäjä tekee näkymissä muutoksia (liittyy kursseille, palauttaa tai arvioi tehtäviä jne.), näkymän tiedot alustetaan uudelleen ilman tarvetta näkymän muulle päivittämiselle.

Kaikissä näkymissä on pyritty tekemään muutoksia ensisijaisesti suoraan tietokantaan alkuperäisen koodin find() metodeilla, sillä pelkkiä olioita käyttämällä aiheutui inkonsistenttisia tilanteita.

Kursseilta poistuttaessa, kurssin opetuksen lopettamisessa, ja tietokannan alustamisessa näytetään myös varmistusikkuna, jossa kysytään haluaako käyttäjä varmasti tehdä halutun päätöksen.

Ohjelmassa voi navigoida takaisin näkymien alareunassa olevan peruuta-painikkeen avulla, tai selaimen omalla painikkeella (kiitos vaatimen Navigator). 
Sivun päivittäminen selaimella ei pitäisi aiheuttaa ongelmia, vaan se vain lataa kyseisen näkymän uudelleen (poikkeuksena kirjautumisen jälkeinen näkymä-vapaa tila, jossa päivittäminen avaa StudentCourses näkymän välttääkseen virheen).

## Alkuperäiseen koodiin tehdyt muutokset

Exercise.java luokasta on muutettu updateDb() metodin palautusarvo booleaniksi, jolloin kannan päivittämisen epäonnistuminen voidaan ilmoittaa käyttäjälle virheenä.
Samasta luokasta on myös korjattu virheellinen sql update-kysely, jossa where lauseessa etsittiin gradeDate:a exerciseId:n sijasta (gradeDate samassa lauseessa).