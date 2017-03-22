package com.foresee.xdeploy.file.testng;

import org.testng.annotations.Test;

import com.foresee.xdeploy.file.PropValue;
import com.foresee.xdeploy.file.rule.ExchangePath;
import com.foresee.xdeploy.file.rule.MappingRule;

import org.testng.annotations.BeforeClass;

public class ExchangePathTest {
	PropValue pv = null;

	@BeforeClass
	public void beforeClass() {
		pv = PropValue.getInstance("/svntools.properties");
	}

	@Test
	public void exchange() {

		try {
			// xx =
			// ExchangePath.exchange("/trunk/engineering/src/gt3nf/web/gt3nf-skin/WebContent/etax/script/attachment/attachment.js");
			// System.out.println(xx);
			//
			// System.out.println(xx.getToZipFolderPath("service"));
			// System.out.println(ExchangePath.exchange("/trunk/engineering/src/gt3nf/web/gt3nf-wsbs/WebContent/forms/TAX_910610010066.txt"));
			// System.out.println(ExchangePath.exchange("trunk/engineering/src/portal/web/gt3nf-admin/src/META-INF/conf/properties/ajaxUpload.filetype.properties"));
			System.out.println(
					ExchangePath.exchange("/trunk/engineering/src/tycx/web/tycx-service/WebContent/WEB-INF/web.xml"));
			System.out.println(
					ExchangePath.exchange("/trunk/engineering/src/tycx/java/com.foresee.tycx.service/ivy.xml"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void getToZipFolderPath() {
		try {
			ExchangePath ep = ExchangePath.exchange(
					"trunk/engineering/src/etax/java/com.foresee.etax.bizfront/src/com/foresee/etax/bizfront/service/IBqbz.java");

			System.out.println(ep);

			System.out.println(ep.getToZipFolderPath("ddd"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void parserJarName() {
		String src = "trunk/engineering/src/etax/java/com.foresee.etax.bizfront/src/com/foresee/etax/bizfront/service/IBqbz.java";
		src = "/trunk/engineering/src/gt3nf/java/gov.chinatax.gt3nf/src/gov/chinatax/gt3nf/dj/tyfy/entry/impl/TyDjOmniBizEntryImpl.java";
		try {
			ExchangePath ep = ExchangePath.exchange(src);
			System.out.println(MappingRule.parserJarName(src));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
