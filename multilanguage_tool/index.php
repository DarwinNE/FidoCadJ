<?php
	switch($_GET['lan']){
		case "it":
			$language = "italian";
		break;
		case "en":
			$language = "english";
		break;
	}

	include_once("languages/".$language."/header.php");
	include_once("languages/".$language."/index.php");
	include_once("languages/".$language."/footer.php");
?>
<!DOCTYPE html>
<html lang='<?php echo $_GET['lan']; ?>'>
	<head>
		<link href='http://fonts.googleapis.com/css?family=Open+Sans:300,400,700' rel='stylesheet'>
		<link href="http://darwinne.github.io/FidoCadJ/db_style.css" rel="stylesheet">
		<meta charset="UTF-8">
		<meta name=viewport content="width=device-width, initial-scale=1">
		<meta name="description" content="<?php echo META_DESCRIPTION; ?>">
		<link rel="icon" href="http://darwinne.github.io/FidoCadJ/images/FidoCadJ_favicon.png" />
		<link rel="canonical" href="http://darwinne.github.io/FidoCadJ/index.html" />
		<title><?php echo PAGE_TITLE; ?></title>
	</head>

	<body>
		<header>
				<a href="index.html"><h1>FidoCadJ</h1></a>
				<h2><?php echo MAIN_SUBTITLE; ?></h2>
				<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<a href="https://twitter.com/share?via=davbucci" class="twitter-share-button">Tweet</a>
				<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+'://platform.twitter.com/widgets.js';fjs.parentNode.insertBefore(js,fjs);}}(document, 'script', 'twitter-wjs');</script>
				<b class="mainmenu">[<?php echo HOME_BUTTON; ?>]</b>
				&nbsp;
				<a href="download.html" class="mainmenu">[<?php echo DOWNLOAD_BUTTON; ?>]</a>
				&nbsp;
				<a href="scrn.html" class="mainmenu">[<?php echo SCREENSHOTS_BUTTON; ?>]</a>
				&nbsp;
				<a href="libs.html" class="mainmenu">[<?php echo LIBRARIES_BUTTON; ?>]</a>
				&nbsp;
				<a href="faq.html" class="mainmenu">[<?php echo FAQ_BUTTON; ?>]</a>
				&nbsp;
				<a href="examples.html" class="mainmenu">[<?php echo EXAMPLES_BUTTON; ?>]</a>
		</header>

		<div id="content">
			<p><b><?php echo FIRST_LINE; ?></p>
			<div id="download_button_container">
				<script>
				    if (navigator.appVersion.indexOf("Mac")!=-1) {
				        document.write('<a href="https://github.com/DarwinNE/FidoCadJ/releases/download/v0.24.5/FidoCadJ_MacOSX.dmg" id="download_button"><?php echo DOWNLOAD_BUTTON_TEXT_1; ?></a>');
				    } else if(navigator.appVersion.indexOf("Win")!=-1) {
				        document.write('<a href="https://github.com/DarwinNE/FidoCadJ/releases/download/v0.24.5/FidoCadJ_Windows.msi" id="download_button"><?php echo DOWNLOAD_BUTTON_TEXT_2; ?></a>');
						} else if(navigator.appVersion.indexOf("Linux")!=-1) {
								document.write('<a href="https://github.com/DarwinNE/FidoCadJ/releases/download/v0.24.5/fidocadj.jar" id="download_button"><?php echo DOWNLOAD_BUTTON_TEXT_3; ?></a>');
						} else if(navigator.appVersion.indexOf("Android")!=-1) {
								document.write('<a href="http://sourceforge.net/projects/fidocadj/files/public_betas/Android/fidocadj-debug20150227.apk/download" id="download_button"><?php echo DOWNLOAD_BUTTON_TEXT_4; ?></a>');
						} else {
				        document.write('<a href="http://darwinne.github.io/FidoCadJ/download.html" id="download_button"><?php echo DOWNLOAD_BUTTON_TEXT_5; ?></a>');
				    }
				</script>
				<p id="another_version_download_link"><a href="#">[<?php echo OTHER_VERSIONS_TEXT; ?>]</a></p>
			</div>
			<ul>
				<li><?php echo LIST_1_ELEMENT_1; ?></li>
				<li><?php echo LIST_1_ELEMENT_2; ?></li>
				<li><?php echo LIST_1_ELEMENT_3; ?></li>
				<li><?php echo LIST_1_ELEMENT_4; ?></li>
				<li><?php echo LIST_1_ELEMENT_5; ?></li>
			</ul>

			<a href="images/screenshots/ecg.png"><img src="http://darwinne.github.io/FidoCadJ/images/screenshots/ecg_t.png" alt="<?php echo IMAGE_ALT_1; ?>" class="indisplay"></a>

			<h3><?php echo SUB_TITLE_1; ?></h3>

			<ul>
				<li> <a href="download.html"><?php echo LIST_2_ELEMENT_1; ?></a>.</li>
				<li><a href="examples.html"><?php echo LIST_2_ELEMENT_2; ?></a>.</li>
				<li><a href="scrn.html"><?php echo LIST_2_ELEMENT_3; ?></a>.</li>
			</ul>

			<h3><?php echo SUB_TITLE_2; ?></h3>
			<p><?php echo PARAGRAPH_1; ?></p>
			<p><?php echo PARAGRAPH_2; ?></p>
			<p><?php echo PARAGRAPH_3; ?>	</p>

			<p><?php echo PARAGRAPH_4; ?>	</p><br>
		</div>
<!-- end of content -->
		<footer>
			<h3><?php echo FOOTER_TITLE; ?></h3>
			<img id="gplv3_logo" src="http://darwinne.github.io/FidoCadJ/images/logos/gplv3-127x51.png" alt="GPL v. 3">
			<p id="license"><?php echo FOOTER_CONTENT; ?></p>
		</footer>
	</body>
</html>
