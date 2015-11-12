<?php
	switch($_GET['lang']){
		case "it":
			$language = "italian";
		break;
		case "en":
			$language = "english";
		break;
	}

	include_once("config.php");
	include_once("languages/".$language."/header.php");
	include_once("languages/".$language."/scrn.php");
	include_once("languages/".$language."/footer.php");
?>
<!DOCTYPE html>
<html lang='<?php echo $_GET['lang']; ?>'>
<html>
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
				<a href="index.html" class="mainmenu">[<?php echo HOME_BUTTON; ?>]</a>
				&nbsp;
				<a class="mainmenu">[<?php echo DOWNLOAD_BUTTON; ?>]</a>
				&nbsp;
				<b href="scrn.html" class="mainmenu">[<?php echo SCREENSHOTS_BUTTON; ?>]</b>
				&nbsp;
				<a href="libs.html" class="mainmenu">[<?php echo LIBRARIES_BUTTON; ?>]</a>
				&nbsp;
				<a href="faq.html" class="mainmenu">[<?php echo FAQ_BUTTON; ?>]</a>
				&nbsp;
				<a href="examples.html" class="mainmenu">[<?php echo EXAMPLES_BUTTON; ?>]</a>
		</header>

		<div id="content">
			<h3><?php echo FIRST_LINE; ?></h3>

			<table class="dtab">

				<tr>
					<td></td>
					<td></td>
				</tr>

				<tr>
					<td>
						<a href="http://darwinne.github.io/FidoCadJ/images/screenshots/microfoni.png"> <img src="http://darwinne.github.io/FidoCadJ/images/screenshots/microfoni_t.png" class="indisplay" alt="<?php echo IMAGE_ALT_1; ?>"></a>
					</td>
					<td>
						<a href="http://darwinne.github.io/FidoCadJ/images/screenshots/microfoni.fcd">microfoni.fcd</a> <br><a href="http://www.electroyou.it/mir/wiki/7-disegnare-con-fidocadj-microfono-a-granuli-di-carbone"><?php echo LINK_1; ?></a>
					</td>
				</tr>

				<tr>
					<td>
					<a href="http://darwinne.github.io/FidoCadJ/images/screenshots/fidocadj.png"> <img src="http://darwinne.github.io/FidoCadJ/images/screenshots/fidocadj_t.png" class="indisplay" alt="<?php echo IMAGE_ALT_2; ?>"></a>
					</td>
					<td><a href="http://darwinne.github.io/FidoCadJ/images/screenshots/eccitatore.fcd">eccitatore.fcd</a></td>
				</tr>

				<tr>
					<td>
						<a href="http://darwinne.github.io/FidoCadJ/images/screenshots/fidocadj_ubuntu.png"> <img src="http://darwinne.github.io/FidoCadJ/images/screenshots/fidocadj_ubuntu_t.png" class="indisplay" alt="<?php echo IMAGE_ALT_3; ?>"></a>
					</td>
					<td>
						<a href="http://darwinne.github.io/FidoCadJ/images/screenshots/eccitatore.fcd">eccitatore.fcd</a>
					</td>
				</tr>

				<tr>
					<td>
						<a href="http://darwinne.github.io/FidoCadJ/images/screenshots/steinmetz.png"> <img src="http://darwinne.github.io/FidoCadJ/images/screenshots/steinmetz_t.png" class="indisplay" alt="<?php echo IMAGE_ALT_4; ?>"></a>
					</td>
					<td>
						<a href="http://darwinne.github.io/FidoCadJ/images/screenshots/steinmetz.fcd">steinmetz.fcd</a><br><a href="http://www.electroyou.it/admin/wiki/breve-storia-illustrata-dell-elettrotecnica-3"><?php echo LINK_2; ?></a>
					</td>
				</tr>

			</table>

			<br>

			<p><?php echo SCREEN_SHOTS_PAGE_UPDATE_DATE; ?>	</p><br>
		</div>
<!-- end of content -->
		<footer>
			<h3><?php echo FOOTER_TITLE; ?></h3>
			<img id="gplv3_logo" src="http://darwinne.github.io/FidoCadJ/images/logos/gplv3-127x51.png" alt="GPL v. 3">
			<p id="license"><?php echo FOOTER_CONTENT; ?></p>
		</footer>
	</body>
</html>
