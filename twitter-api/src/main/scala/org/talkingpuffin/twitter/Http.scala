package org.talkingpuffin.twitter

import scala.xml.{Node, XML}
import org.apache.log4j.Logger
import org.apache.commons.codec.binary.Base64
import java.net.{URL, HttpURLConnection, URLEncoder}
import java.io.{DataOutputStream, BufferedReader, InputStreamReader}

/**
* Handles HTTP requests.
*/
class Http(user: String, password: String){
  private val log = Logger.getLogger("Http")

  /** the encoded authentication string.  This is null if user or password is null. */
  val encoding = if(user != null && password != null) new String(Base64.encodeBase64((user + ":" + password).getBytes())) else null
  
  /**
  * Fetch an XML document from the given URL
  */
  def doGet(url: URL): Node = {
    val conn = url.openConnection.asInstanceOf[HttpURLConnection]
    log.debug("GET " + url)
    if(encoding != null){
      conn.setRequestProperty ("Authorization", "Basic " + encoding);
    }
    getXML(conn)
  }

  def doDelete(url: URL) = {
    val conn = url.openConnection.asInstanceOf[HttpURLConnection]
    log.debug("DELETE " + url)

    if(encoding != null){
      conn.setRequestProperty ("Authorization", "Basic " + encoding);
    }
    conn.setRequestMethod("DELETE")
    getXML(conn)
  }
  /*
  * post to the specified URL with the given params, return an XML node built from the response
  * @param url the URL to post to
  * @param params a List of String tuples, the first entry being the param, the second being the value
  */
  def doPost(url: URL, params: List[(String,String)]): Node = {
    val conn = url.openConnection.asInstanceOf[HttpURLConnection]
    log.debug("POST " + url)
    if(encoding != null){
      conn.setRequestProperty ("Authorization", "Basic " + encoding);
    }
    conn.setDoInput(true)
    conn.setRequestMethod("POST")
    val content = buildParams(params)

    if(content != null){
      conn.setUseCaches(false)
      conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      conn.setDoOutput(true)
      val printout = new DataOutputStream(conn.getOutputStream())
      if(content != null){
        printout.writeBytes (content)
        printout.flush ()
        printout.close ()
      }
    }
    getXML(conn)
  }
  
  /*
  * take an opened (and posted to, if applicable) connection, read the response code, and take appropriate action.
  * If the response code is 200, return an XML node built on the response.
  * If the response code is anything else, throw a new TwitterException based on the code. 
  * This path also reads from conn.getErrorStream() to populate the twitterMessage field
  * in the thrown exception.
  */
  private def getXML(conn: HttpURLConnection): Node = {
    val response = conn.getResponseCode()
    response match {
      case 200 => XML.load(conn.getInputStream())
      case _ => throw TwitterException({
          var errMsg = ""
          val reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))
          var line = reader.readLine()
          while(line != null){
            errMsg += line
            line = reader.readLine()
          }
          errMsg  
        },response)
    }
  }

  private def buildParams(params: List[(String,String)]): String = {
    params match {
      case Nil => null
      case (param,value) :: rest => {
        val end = buildParams(rest)
        param + "=" + URLEncoder.encode(value, "UTF-8") + (if (end == null) "" else "&" + end)
      }
    }
  }
}

