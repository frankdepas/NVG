package es.upm.dit.isst.geonote;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import es.upm.dit.isst.geonote.dao.UserDAOImpl;

@SuppressWarnings("serial")
public class TestUserServlet extends HttpServlet {
	

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
		throws IOException{
		UserDAOImpl.getInstance().add("+34684314092", "3067804521", "token=3067804521-9AAHv59XBefX0kslVwryWpp6dHsfxHN2q4qGZFx,secret=NTU45DJ6TScTnPS1ggFwTFpg2GF5yWmkOTtuwYa7Bogus",
				"", "Carlos Scovino");
		UserDAOImpl.getInstance().add("+34649525303", "3067804521", "token=3067804521-9AAHv59XBefX0kslVwryWpp6dHsfxHN2q4qGZFx,secret=NTU45DJ6TScTnPS1ggFwTFpg2GF5yWmkOTtuwYa7Bogus",
				"", "Diego Arespacochaga");
		UserDAOImpl.getInstance().add("+34667216362", "3067804521", "token=3067804521-9AAHv59XBefX0kslVwryWpp6dHsfxHN2q4qGZFx,secret=NTU45DJ6TScTnPS1ggFwTFpg2GF5yWmkOTtuwYa7Bogus",
				"", "Eloy Ramirez");
		UserDAOImpl.getInstance().add("+34653285277", "3067804521", "token=3067804521-9AAHv59XBefX0kslVwryWpp6dHsfxHN2q4qGZFx,secret=NTU45DJ6TScTnPS1ggFwTFpg2GF5yWmkOTtuwYa7Bogus",
				"", "Frank De Pasquale");
		UserDAOImpl.getInstance().add("+34635411725", "3067804521", "token=3067804521-9AAHv59XBefX0kslVwryWpp6dHsfxHN2q4qGZFx,secret=NTU45DJ6TScTnPS1ggFwTFpg2GF5yWmkOTtuwYa7Bogus",
				"", "Daniel Serrano");
	}

}
