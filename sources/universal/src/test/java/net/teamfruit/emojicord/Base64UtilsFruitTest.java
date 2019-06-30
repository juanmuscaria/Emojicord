package net.teamfruit.emojicord;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class Base64UtilsFruitTest {
	@Before
	public void setUp() throws Exception {
	}

	@Parameters
	public static Object[] params() {
		return new Object[] {
				"239603279762423808",
				"244817555456393216",
				"245831611050885121",
				"246257786176929792",
				"251836953392054273",
				"255928708194369537",
				"257184784344809473",
				"264244926311563265",
				"279881829870206976",
				"284978139350827009",
				"290904634854342656",
				"310018698536812545",
				"310074677248786432",
				"316567884950274050",
				"318329750143893504",
				"318675187640041472",
				"319397904819421184",
				"320776094380982273",
				"321978958503608320",
				"323020147826622465",
				"327041506630303754",
				"327695831786323969",
				"327762950469451786",
				"327809728866549771",
				"332181988633083925",
				"339052624886104064",
				"352466772076658698",
				"353010676228423680",
				"353470152521678848",
				"356310109422354433",
				"361392492102418432",
				"363321804187500544",
				"381627222055845918",
				"408732714007789569",
				"411198377674670080",
				"419611990839787531",
				"424511484585050112",
				"424512290771959808",
				"424514504949497857",
				"424516974568144896",
				"424519198828658698",
				"424524435396100096",
				"426942367887523840",
				"431691467602526208",
				"437196885267054603",
				"439721428682539010",
				"441912411143012353",
				"450233329653121034",
				"451694123779489792",
				"451707948842876939",
				"455201493478277130",
				"455218489406259200",
				"455218536411955202",
				"455218594784083981",
				"456781995771559937",
				"457852092456108039",
				"467357026348171265",
				"468435329570045972",
				"472570872641617950",
				"473813236072841216",
				"485823903596544000",
				"490900204321505281",
				"493303627843239948",
				"500161520429432833",
				"513406664716845067",
				"532256022002270211",
				"540152509176479748",
				"540499740421521410",
				"576027286738829312",
		};
	}

	@Parameter(0)
	public String emoji;

	@Test
	public void test() {
		final long emojil = Long.parseLong(this.emoji);
		Log.log.info(this.emoji);
		Log.log.info(Base64Utils.encode1(emojil) + " : "
				+ Base64Utils.encode2(emojil) + " : "
				+ Base64Utils.encode3(emojil));
	}
}
