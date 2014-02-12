
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>

<html>

<%@page import="org.jboss.*"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
  <head>
    <meta charset="UTF-8" />
    <title>Geolocation and Google Maps API</title>
	<link rel="stylesheet" type="text/css" href="main.css">	
    <script src="http://maps.google.com/maps/api/js?sensor=true"></script>
    <script>
      function writeAddressName(latLng) {
        var geocoder = new google.maps.Geocoder();
        geocoder.geocode({
          "location": latLng
        },
        function(results, status) {
          if (status == google.maps.GeocoderStatus.OK){
            document.getElementById("address").innerHTML = results[0].formatted_address;
            var country;
            var postcode;
            var locality;
            var street;
            var state;

            for (i=0;i<results[0].address_components.length;i++){
                for (j=0;j<results[0].address_components[i].types.length;j++){
                   if(results[0].address_components[i].types[j]=="country")
    {
                      country = results[0].address_components[i].long_name
    ;
                    }
                    else if(results[0].address_components[i].types[j]=="postal_code"){
                      postcode = results[0].address_components[i].long_name;
                      }
                      else if(results[0].address_components[i].types[j]=="sublocality" || results[0].address_components[i].types[j]=="locality"){
                      locality = results[0].address_components[i].long_name;
                      }
                       else if(results[0].address_components[i].types[j]=="route"){
                    street = results[0].address_components[i].long_name;
                      } else if(results[0].address_components[i].types[j]=="administrative_area_level_1" || results[0].address_components[i].types[j]=="administrative_area_level_2"){
                    state = results[0].address_components[i].long_name;
                      }
                      
            }
            }
           
            document.getElementById("geocodeterms").innerHTML="<b>Street: </b> " + street.toString() +
            "<br><b>Town/City:</b> " + locality.toString() +
            "<br><b>County/State: </b> " + state.toString() +
            "<br><b>Country: </b>" + country.toString() +
            "<br><b> Postcode: </b>" + postcode.toString();
            document.getElementById("townterm").value= locality.toString();
            document.getElementById("streetterm").value = street.toString();
            document.getElementById("stateterm").value = state.toString();
            document.getElementById("countryterm").value = country.toString();
            document.getElementById("postcodeterm").value = postcode.toString();
          }
          else
            document.getElementById("error").innerHTML += "Unable to retrieve your address" + "<br />";
        });
      }
 
      function geolocationSuccess(position) {
        var userLatLng = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
        // Write the formatted address
        writeAddressName(userLatLng);
 
        var myOptions = {
          zoom : 18,
          center : userLatLng,
          mapTypeId : google.maps.MapTypeId.ROADMAP
        };
        // Draw the map
        var mapObject = new google.maps.Map(document.getElementById("map"), myOptions);
        // Place the marker
        new google.maps.Marker({
          map: mapObject,
          position: userLatLng
        });

      }
 
      function geolocationError(positionError) {
        document.getElementById("error").innerHTML += "Error: " + positionError.message + "<br />";
      }
 
      function geolocateUser() {
        // If the browser supports the Geolocation API
        if (navigator.geolocation)
        {
          var positionOptions = {
            enableHighAccuracy: true,
            timeout: 10 * 1000 // 10 seconds
          };
          navigator.geolocation.getCurrentPosition(geolocationSuccess, geolocationError, positionOptions);
        }
        else
          document.getElementById("error").innerHTML += "Your browser doesn't support the Geolocation API";
      }
 
      window.onload = geolocateUser;
    </script>
    
  </head>
  <body>
    <h1>Geolocation basic example</h1>
    <div id="map"></div>
    <p><b>Full Address</b>: <span id="address"></span></p>
    <p><span id="geocodeterms"></span></p>
    <p><span id="town"></span></p>
    <p id="error"></p>
    <h2>PdfBox on GAE - Limit of 4 page extraction</h2>
	
<%
	String pdfurl = request.getParameter("pdfurl");
	String x = request.getParameter("X");
	String y = request.getParameter("Y");
	String w = request.getParameter("W");
	String h = request.getParameter("H");
	String manualterm = request.getParameter("term");
	String term = request.getParameter("street");
	String term2 = request.getParameter("town");
	String term3 = request.getParameter("state");
	String term4 = request.getParameter("country");
	String term5 = request.getParameter("postcode");

	if (pdfurl == null)
		pdfurl = "";
	if (x == null)
		x = "0";
	if (y == null)
		y = "0";
	if (w == null)
		w = "500";
	if (h == null)
		h = "800";

	String captchaError = "";

%>
<br />
<div style="background: #FFFFBB; display: inline-block; padding: 10px;">

<form method="post">Pdf url: <input type="text" name="pdfurl"
	value="<%=pdfurl%>" style="width: 400px;" /><br />
	Manual Search term: <input type="text" name="term" value="<%=manualterm%>"/> <br/>
	Search terms: 
	<input readonly="readonly" type="text" id="streetterm" name="street" value="<%=term%>"/>
	<input readonly="readonly"type="text" id="townterm" name="town" value="<%=term2%>"/> <br/>
	<input readonly="readonly"type="text" id="stateterm" name="state" value="<%=term3%>"/>
	<input readonly="readonly"type="text" id="countryterm" name="country" value="<%=term4%>"/>
	<input readonly="readonly"type="text" id="postcodeterm" name="postcode" value="<%=term5%>"/>
	<input type="hidden" name="X" value="<%=x%>"
	style="width: 50px;" /> <input type="hidden" name="Y" value="<%=y%>"
	style="width: 50px;" /> <input type="hidden" name="W" value="<%=w%>"
	style="width: 50px;" /> <input type="hidden" name="H" value="<%=h%>"
	style="width: 50px;" /> 

<input type="submit" name="btnSumbit" value="Get text!" /></form>
</div>
<br />
<%
	if (request.getParameter("btnSumbit") != null) {
		if (captchaError.equals("")) {

	Date startDate = new Date();
	int xx = Integer.parseInt(x);
	int yy = Integer.parseInt(y);
	int ww = Integer.parseInt(w);
	int hh = Integer.parseInt(h);
	ArrayList<String> geoterms = new ArrayList<String>();
	geoterms.add(term);
	geoterms.add(term2);
	geoterms.add(term3);
	geoterms.add(term4);
	geoterms.add(term5);
	geoterms.add(manualterm);
	String pdfText = org.jboss.as.quickstarts.helloworld.PdfBoxGAEDemo.Exec(pdfurl, xx, yy, ww, hh, geoterms);
		

	Date endDate = new Date();
	double deltaSeconds = (endDate.getTime() - startDate.getTime()) / 1000.0;
%><br />
<br />
Time (Http GET + text extraction):
<%=deltaSeconds%>
s
<br />
<br />
Extracted text:
<br/>
<a href="#<%=manualterm%>"><%=manualterm%></a> <br/>
<a href="#<%=term%>"><%=term%></a> <br/>
<a href="#<%=term2%>"><%=term2%></a>  <br/>
<a href="#<%=term3%>"><%=term3%></a> <br/>
<a href="#<%=term4%>"><%=term4%></a> <br/>
<a href="#<%=term5%>"><%=term5%></a> <br/>
<div style="background: #e0e0e0;"><pre><%=pdfText%></pre></div>


<br />
<br />
Extracted text v2:
<table>
	<tr>
		<td><%=term%></td>
		<td>Stuff2</td>
	</tr>
	<tr>
		<td>2Stuff</td>
		<td>2Stuff2</td>
	</tr>
</table>
<%
	}
	}
%>

<a href="http://code.google.com/appengine/" target="_blank"> <img
	src="http://code.google.com/appengine/images/appengine-noborder-120x30.gif"
	alt="Powered by Google App Engine" border="0" /> </a>

  </body>
</html>
