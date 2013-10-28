package ch.fhnw.apsi.lab1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class FileCreator {

	HashMap<Integer, ArrayList<String>> map = new HashMap<>();
	HashMap<Integer, ArrayList<String>> letters = new HashMap<>();

	private void fillMap() {

		ArrayList<String> al0 = new ArrayList<>();
		al0.add("Meine");
		al0.add("");
		map.put(0, al0);

		ArrayList<String> al1 = new ArrayList<>();
		al1.add("vom Herzen");
		al1.add("aufrichtig");
		map.put(1, al1);

		ArrayList<String> al2 = new ArrayList<>();
		al2.add("den");
		al2.add("Deinen");
		map.put(2, al2);

		ArrayList<String> al3 = new ArrayList<>();
		al3.add("erfreulichen");
		al3.add("sehr willkommen");
		map.put(3, al3);

		ArrayList<String> al4 = new ArrayList<>();
		al4.add("Lieferungsvertrag");
		al4.add("Auftrag");
		map.put(4, al4);

		ArrayList<String> al5 = new ArrayList<>();
		al5.add("Dich");
		al5.add("Deine Firma");
		map.put(5, al5);

		ArrayList<String> al6 = new ArrayList<>();
		al6.add("vertraulich");
		al6.add("");
		map.put(6, al6);

		ArrayList<String> al7 = new ArrayList<>();
		al7.add("wir");
		al7.add("unsere Ingenieure");
		map.put(7, al7);

		ArrayList<String> al8 = new ArrayList<>();
		al8.add("Herausragendes");
		al8.add("Sensationelles");
		map.put(8, al8);

		ArrayList<String> al9 = new ArrayList<>();
		al9.add("Köchern");
		al9.add("Reagenzgläsern");
		map.put(9, al9);

		ArrayList<String> al10 = new ArrayList<>();
		al10.add("halten");
		al10.add("haben");
		map.put(10, al10);

		ArrayList<String> al11 = new ArrayList<>();
		al11.add("plastisch");
		al11.add("");
		map.put(11, al11);

		ArrayList<String> al12 = new ArrayList<>();
		al12.add("denken");
		al12.add("vorstellen");
		map.put(12, al12);

		ArrayList<String> al13 = new ArrayList<>();
		al13.add("dahinten");
		al13.add("darin");
		map.put(13, al13);

		ArrayList<String> al14 = new ArrayList<>();
		al14.add("einzige");
		al14.add("erste");
		map.put(14, al14);

		ArrayList<String> al15 = new ArrayList<>();
		al15.add("grosse");
		al15.add("ansehnliche");
		map.put(15, al15);

		ArrayList<String> al16 = new ArrayList<>();
		al16.add("Menge");
		al16.add("Anzahl");
		map.put(16, al16);

		ArrayList<String> al18 = new ArrayList<>();
		al18.add("Fläschen");
		al18.add("Muster");
		map.put(17, al18);

		ArrayList<String> al19 = new ArrayList<>();
		al19.add("kostenlos");
		al19.add("gratis");
		map.put(18, al19);

		ArrayList<String> al20 = new ArrayList<>();
		al20.add("kriegen");
		al20.add("erhalten");
		map.put(19, al20);

		ArrayList<String> al21 = new ArrayList<>();
		al21.add("die Summe");
		al21.add("den Betrag");
		map.put(20, al21);

		ArrayList<String> al22 = new ArrayList<>();
		al22.add("Schweizer Franken");
		al22.add("CHF");
		map.put(21, al22);

		ArrayList<String> al23 = new ArrayList<>();
		al23.add("mit der");
		al23.add("");
		map.put(22, al23);

		ArrayList<String> al24 = new ArrayList<>();
		al24.add("von der");
		al24.add("der");
		map.put(23, al24);

		ArrayList<String> al25 = new ArrayList<>();
		al25.add("in");
		al25.add("4001");
		map.put(24, al25);

		ArrayList<String> al26 = new ArrayList<>();
		al26.add("CH");
		al26.add("");
		map.put(25, al26);

		ArrayList<String> al27 = new ArrayList<>();
		al27.add("hoffe");
		al27.add("freue mich");
		map.put(26, al27);

		ArrayList<String> al28 = new ArrayList<>();
		al28.add("geholfen");
		al28.add("gedient");
		map.put(27, al28);

		ArrayList<String> al29 = new ArrayList<>();
		al29.add("schliesse ich");
		al29.add("verbleibe ich");
		map.put(28, al29);

		ArrayList<String> al30 = new ArrayList<>();
		al30.add("lieben");
		al30.add("freundlichen");
		map.put(29, al30);

		ArrayList<String> al31 = new ArrayList<>();
		al31.add("Dein");
		al31.add("");
		map.put(30, al31);

		ArrayList<String> al32 = new ArrayList<>();
		al32.add("Geschäftsführer");
		al32.add("CEO");
		map.put(31, al32);
	}

	public void readFile(String path) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line;
		while ((line = br.readLine()) != null) {
			processLine(line);
		}
		br.close();
	}

	private void processLine(String line) {
		String tmpLine = new String(line);
		int hashIndex = tmpLine.indexOf('#');
		while (hashIndex != -1) {
			int placeHolder = Integer
					.parseInt(line.substring(hashIndex + 1, 1));
			tmpLine = tmpLine.substring(hashIndex + 2);
			hashIndex = tmpLine.indexOf('#');
			createVariation(hashIndex,placeHolder,tmpLine.substring(beginIndex))
		}

	}

	private void createVariation(int hashIndex, int placeHolder) {
		ArrayList<String> possibilities = map.get(hashIndex);
		if (placeHolder > 0) {
			ArrayList<String> lastVariants = letters.get(placeHolder - 1);
			for (String lv : lastVariants) {
				for (String pos : possibilities) {
					ArrayList<String> newPermutations = new ArrayList<>();
					
				}
			}
		
		}

	}

}
