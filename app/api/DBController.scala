package api

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.mvc.{Action, Controller}
import play.api.Play.current

class DBController extends Controller {
  def index = Action {
    DB.withConnection { implicit connection =>
      /* Executing SQL Queries */
      val result1 = SQL("SELECT 1").execute()

      val result2 = SQL("DELETE FROM City WHERE id = 99").executeUpdate()

      val result3 = SQL("""
          INSERT INTO City(name, country)
          VALUES ({name}, {country})
        """)
        .on('name -> "Cambridge", 'country -> "New Zealand")
        .executeInsert(str("ids").+)

      val d: Symbol = 'name

      val result4 = SQL("SELECT * FROM Cities WHERE id = {id}")
        .on('id -> 1)
        .as(str("Cities.name").single)

      // Parsing column by name or position
      val parser =
        str("name") ~ float(3) map { case name ~ f => name -> f }

      val id = 1
      val result5 = SQL"SELECT * FROM Prod WHERE id = ${id + 1}"
        .as(parser.single)

      /*
      * By using #$value instead of $value, interpolated value will be part of the prepared statement,
      * rather being passed as a parameter when executing this SQL statement
      *
      * */
      val cmd = "SELECT"
      val table = "Test"
      val result6 = SQL"#$cmd * FROM #$table WHERE id = ${"id1"} AND code IN (${Seq(2, 5)})"


      /* Streaming Results */
      // Query results can be processed row per row, not having all loaded in memory
      val countryCount = SQL"SELECT count(*) AS c FROM Country".fold(0L) { (c, _) => c + 1 }

      // Results can also be partially processed
//      val books = SQL"SELECT name FROM Books".foldWhile(List[String]()) { (list, row) =>
//        if (list.size == 100) (list -> false)
//        else (list := row[String]("name")) -> true
//      }

      // It's also possible to use a custom streaming

      // on purpose, multi-value parameter must strictly be declared with one of supported types.
      // Value of a subtype must be passed as parameter with supported
      val seq = IndexedSeq("a", "b", "c")

//      val wrong = SQL"SELECT * FROM Test WHERE cat in ($seq)"
      val right = SQL"SELECT * FROM Test WHERE cat in (${seq: Seq[String]}})"

      val result7 = SQL"SELECT str_arr FROM tbl".as(scalar[List[String]].*)
      val result8 = SQL"SELECT str_arr FROM tbl".as(list[String]("str_arr").*)


      /* Using Pattern Matching */

      val id1 = 10
      val countries = SQL"SELECT name, population FROM Country WHERE id = $id1"
        .map {
          case Row("France", _) => 1
          case Row(name: String, pop: Int) => 2
        }
        .as(scalar[Int].*)

      /* Using for comprehension */
      val parser1 = for {
        a <- str("colA")
        b <- int("colB")
      } yield (a, b)

      val parsed = SQL"SELECT * FROM Test".as(parser1.single)

      /* Retrieving data along with execution context */
      // Moreover data, query execution involves context information like SQL warnings that may be raised especially when working with stored SQL procedure

      val res: SqlQueryResult = SQL"EXEC stored_proc ${"code"}".executeQuery()

      val resString: Option[String] = res.statementWarning match {
        case Some(warning) =>
          warning.printStackTrace()
          None
        case _ =>
          res.as(scalar[String].singleOpt)
      }


      /* Working with optional/nullable values */
      val parser2 = str("name") ~ get[Option[Int]]("indepYear") map {
        case n ~ y => 1
      }

      val sql = SQL"SELECT name, indepYear FROM Country"
      val res2 = sql.as(parser2.*)

      SQL"INSERT INTO Test(title) VALUES (${Option.empty[String]}})"

      /* Using the Parser API */

      val rowParser = (
        str("name") ~
        int("population"))
        .map(flatten)

      def display(name: String, pop: Int) = s"The population in $name is of $pop"

      val parser3 = str("name") ~ int("pop") map to(display _)

      val string = SQL"SELECT * FROM Test".as((int("id") ~> str("val")).single)

    }

    Ok("good")
  }

  def languages = Action {
    DB.withConnection { implicit connection =>

      case class SpokenLanguges(country: String, officialLanguages: Option[String], otherLanguages: Seq[String])

      def spokenLanguages(countryCode: String): Option[SpokenLanguges] = {
        val parser = str("name") ~ str("language") ~ str("isOfficial") map {
          case n ~ l ~ "T" => (n, l, true)
          case n ~ l ~ "F" => (n, l, false)
        }

        val languages = SQL"""
          SELECT * FROM Country c
          JOIN CountryLanguage l ON l.country_code = c.code
          WHERE c.code = $countryCode
        """
        .as(parser.*)

        languages.headOption.map { f =>
          SpokenLanguges(
            f._1,
            languages.find(_._3).map(_._2),
            languages.filterNot(_._3).map(_._2)
          )
        }
      }
    }

    Ok("good")
  }




}
