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
	include_once("languages/".$language."/libs.php");
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
				<br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;				<a href="index.html" class="mainmenu">[<?php echo HOME_BUTTON; ?>]</a>
				&nbsp;
				<a href="download.html" class="mainmenu">[<?php echo DOWNLOAD_BUTTON; ?>]</a>
				&nbsp;
				<a href="scrn.html" class="mainmenu">[<?php echo SCREENSHOTS_BUTTON; ?>]</a>
				&nbsp;
				<b class="mainmenu">[<?php echo LIBRARIES_BUTTON; ?>]</b>
				&nbsp;
				<a href="faq.html" class="mainmenu">[<?php echo FAQ_BUTTON; ?>]</a>
				&nbsp;
				<a href="examples.html" class="mainmenu">[<?php echo EXAMPLES_BUTTON; ?>]</a>
		</header>

		<div id="content">
			<h3><?php echo FIRST_LINE; ?></h3>
			<p><?php echo PARAGRAPH_1; ?></p>

			<table class="dtab">
				<tr>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
				</tr>

				<tr>
					<td>
						<a href="http://darwinne.github.io/FidoCadJ/libs/LaddEYr.zip">LaddEYr (zip)</a><br>
					</td>
					<td>
						2 KiB
					</td>
					<td>
						1.0
					</td>
					<td>
						<?php echo LIBRARY_1; ?>
					</td>
				</tr>

				<tr>
					<td>
						<a href="http://darwinne.github.io/FidoCadJ/libs/MusiCadJEY.zip">MusiCadJEY (zip)</a><br>
					</td>
					<td>
						8 KiB
					</td>
					<td>
						1.0
					</td>
					<td>
						<?php echo LIBRARY_2; ?>
					</td>
				</tr>

				<tr>
					<td>
						<a href="http://darwinne.github.io/FidoCadJ/libs/Pneumatica.zip">Pneumatica (zip)</a><br>
					</td>
					<td>
						5 KiB
					</td>
					<td>
						1.0
					</td>
					<td>
						<?php echo LIBRARY_3; ?>
					</td>
				</tr>

				<tr>
					<td>
						<a href="http://darwinne.github.io/FidoCadJ/libs/imec_symbols.zip">Imec symbols (zip)</a><br>
					</td>
					<td>
						8 KiB
					</td>
					<td>
						1.0
					</td>
					<td>
						<?php echo LIBRARY_4; ?>
					</td>
				</tr>

				<tr>
					<td>
						<a href="http://darwinne.github.io/FidoCadJ/libs/flowchart.zip">flowchart (zip)</a><br>
					</td>
					<td>
						4 KiB
					</td>
					<td>
						1.0
					</td>
					<td>
						<?php echo LIBRARY_5; ?>
					</td>
				</tr>

				<tr>
					<td>
						<a href="http://darwinne.github.io/FidoCadJ/libs/Simboli_Idraulici.zip">Simboli_Idraulici (zip)</a><br>
					</td>
					<td>
						4 KiB
					</td>
					<td>
						1.0
					</td>
					<td>
						<?php echo LIBRARY_6; ?>
					</td>
				</tr>

				<tr>
					<td>
						<a href="http://darwinne.github.io/FidoCadJ/libs/PIERIN_PIC18_Library_for_FidoCadJ.zip">PIERIN PIC18 Library (zip)</a><br>
					</td>
					<td>
						4 KiB
					</td>
					<td>
						1.0
					</td>
					<td>
						<?php echo LIBRARY_7; ?>
				</tr>

				<tr>
					<td>
						<a href="http://darwinne.github.io/FidoCadJ/libs/lib_agg.zip">lib_agg (zip)</a><br>
					</td>
					<td>
						12 KiB
					</td>
					<td>
						1.0
					</td>
					<td>
						<?php echo LIBRARY_8; ?>
					</td>
				</tr>

				<tr>
					<td>
						<a href="http://darwinne.github.io/FidoCadJ/libs/celsius_lib.zip">celsius_lib (zip)</a><br>
					</td>
					<td>
						16 KiB
					</td>
					<td>
						1.0
					</td>
					<td>
						<?php echo LIBRARY_9; ?>
					</td>
				</tr>

				<tr>
					<td>
						<a href="http://darwinne.github.io/FidoCadJ/libs/ihram.zip">ihram (zip)</a><br>
					</td>
					<td>
						16 KiB
					</td>
					<td>
						3.0
					</td>
					<td>
						<?php echo LIBRARY_10; ?>
					</td>
				</tr>

				<tr>
					<td>
						<a href="http://darwinne.github.io/FidoCadJ/libs/lib_tullio.zip">lib_tullio (zip)</a><br>
					</td>
					<td>
						8 KiB
					</td>
					<td>
						1.0
					</td>
					<td>
						<?php echo LIBRARY_11; ?>
					</td>
				</tr>

				<tr>
					<td>
						<a href="http://darwinne.github.io/FidoCadJ/libs/libs_2_2012.zip">libs_2_2012 (zip)</a> <br>
					</td>
					<td>
						136 KiB
					</td>
					<td>
						02/2012
					</td>
					<td>
						<?php echo LIBRARY_12; ?>
					</td>
				</tr>

				<tr>
					<td>
						<a href="http://darwinne.github.io/FidoCadJ/libs/SSOP_corretti.zip">SSOP_corretti (zip)</a>
					</td>
					<td>
						4 KiB
					</td>
					<td>
						13/02/2010
					</td>
					<td>
						<?php echo LIBRARY_13; ?>
					</td>
				</tr>

				<tr>
					<td>
						<a href="http://darwinne.github.io/FidoCadJ/libs/ingcivile.zip">ingcivile (zip)</a>
					</td>
					<td>
						4 KiB
					</td>
					<td>
						0.1, 10/03/2010
					</td>
					<td>
						<?php echo LIBRARY_14; ?>
					</td>
				</tr>

				<tr>
					<td>
						<a href="http://darwinne.github.io/FidoCadJ/libs/MarioPCB.zip">MarioPCB (zip)</a><br>
					</td>
					<td>
						4 KiB
					</td>
					<td>
						1.0
					</td>
					<td>
						<?php echo LIBRARY_15; ?>
					</td>
				</tr>

				<tr>
					<td>
					<a href="http://darwinne.github.io/FidoCadJ/libs/elettrotecnica.zip">elettrotecnica (zip)</a><br>
					</td>
					<td>
						8 KiB
					</td>
					<td>
						1.0
					</td>
					<td>
						<?php echo LIBRARY_16; ?>
					</td>
				</tr>

				<tr>
					<td>
						<a href="http://darwinne.github.io/FidoCadJ/libs/arduino.zip">arduino (zip)</a><br>
					</td>
					<td>
						4 KiB
					</td>
					<td>
						1.0
					</td>
					<td>
						<?php echo LIBRARY_17; ?>
					</td>
				</tr>

				<tr>
					<td>
						<a href="https://github.com/simon-zz/simon-zz-fidocadj-libs/archive/master.zip">simon-zz (zip)</a><br>
					</td>
					<td>
						7 KiB
					</td>
					<td>
						1.0
					</td>
					<td>
						<?php echo LIBRARY_18; ?>
					</td>
				</tr>

			</table>

			<p><?php echo PARAGRAPH_2; ?></p>

			<p><?php echo LIBRARIES_PAGE_UPDATE_DATE; ?></p><br>
		</div>
<!-- end of content -->
		<footer>
			<h3><?php echo FOOTER_TITLE; ?></h3>
			<img id="gplv3_logo" src="http://darwinne.github.io/FidoCadJ/images/logos/gplv3-127x51.png" alt="GPL v. 3">
			<p id="license"><?php echo FOOTER_CONTENT; ?>
			</p>
		</footer>
	</body>
</html>
<?php file_put_contents("php_to_html_output/".$_GET['lang']."/".str_replace(".php",".html",basename(__FILE__)), ob_get_clean()); ?>
