<?php ob_start(); ?>
<?php
	switch($_GET['lang']){
		case "it":
			$language = "italian";
		break;
		case "en":
			$language = "english";
		break;
		case "gr":
			$language = "greek";
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
				<a href="<?php echo "http://darwinne.github.io/FidoCadJ/".str_replace(".php",".html",basename(__FILE__)); ?>"><img src="http://darwinne.github.io/FidoCadJ/images/flags/english_flag.jpg" alt="<?php echo ENGLISH_FLAG_IMAGE_ALT; ?>"/></a>
				&nbsp;
				<a href="<?php echo "http://darwinne.github.io/FidoCadJ/lang/it/".str_replace(".php",".html",basename(__FILE__)); ?>"> <img src="http://darwinne.github.io/FidoCadJ/images/flags/italian_flag.jpg" alt="<?php echo ITALIAN_FLAG_IMAGE_ALT; ?>"/></a>
				&nbsp;
				<a href="<?php echo "http://darwinne.github.io/FidoCadJ/lang/gr/".str_replace(".php",".html",basename(__FILE__)); ?>"> <img src="http://darwinne.github.io/FidoCadJ/images/flags/greek_flag.jpg" alt="<?php echo GREEK_FLAG_IMAGE_ALT; ?>"/></a>
				<br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<a href="index.html" class="mainmenu">[<?php echo HOME_BUTTON; ?>]</a>
				&nbsp;
				<a href="download.html" class="mainmenu">[<?php echo DOWNLOAD_BUTTON; ?>]</a>
				&nbsp;
				<b class="mainmenu">[<?php echo SCREENSHOTS_BUTTON; ?>]</b>
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
<?php file_put_contents("php_to_html_output/".$_GET['lang']."/".str_replace(".php",".html",basename(__FILE__)), ob_get_clean()); ?>
