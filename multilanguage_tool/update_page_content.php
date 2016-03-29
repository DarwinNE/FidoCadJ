<?php
echo "<h1>This page updates all files in php_to_html_output directory at once.</h1>";
echo "<p>Several pages may be open on your browser during the process</p>";

foreach (glob("*.*") as $filename) {
  if($filename != "config.php" and $filename != "update_page_content.php"){
    echo $filename." - english<br />";
    echo '<iframe src="'.$filename.'?lang=en" height="3%" width="25%" ></iframe><br />'; //rendering of italian pages
    echo $filename." - italian<br />";
    echo '<iframe src="'.$filename.'?lang=it" height="3%" width="25%" ></iframe><br />'; //rendering of italian pages
  }
}
echo "<p>The translation process is completed, now you can find the pages in <b>multilanguage_tool/php_to_html_output/</b></p>";

?>
