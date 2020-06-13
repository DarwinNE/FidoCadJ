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
	include_once("languages/".$language."/download.php");
	include_once("languages/".$language."/footer.php");
?>


<!DOCTYPE html>
<html lang='<?php echo $_GET['lang']; ?>'>
	<head>
		<link href='http://fonts.googleapis.com/css?family=Open+Sans:300,400,700' rel='stylesheet'>
		<link href="http://darwinne.github.io/FidoCadJ/db_style.css" rel="stylesheet">
		<meta charset="UTF-8">
		<meta name=viewport content="width=device-width, initial-scale=1">
		<meta name="description" content="<?php echo DOWNLOAD_PAGE_DESCRIPTION; ?>">
		<link rel="icon" href="http://darwinne.github.io/FidoCadJ/images/FidoCadJ_favicon.png" />
		<link rel="canonical" href="http://darwinne.github.io/FidoCadJ/download.html" />
		<title><?php echo TITLE_PAGE_DESCRIPTION; ?></title>
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
				<b class="mainmenu">[<?php echo DOWNLOAD_BUTTON; ?>]</b>
				&nbsp;
				<a href="scrn.html" class="mainmenu">[<?php echo SCREENSHOTS_BUTTON; ?>]</a>
				&nbsp;
				<a href="libs.html" class="mainmenu">[<?php echo LIBRARIES_BUTTON; ?>]</a>
				&nbsp;
				<a href="faq.html" class="mainmenu">[<?php echo FAQ_BUTTON; ?>]</a>
				&nbsp;
				<a href="examples.html" class="mainmenu">[<?php echo EXAMPLES_BUTTON; ?>]</a>
				&nbsp;
		</header>
		<div id="content">
				<h3><?php echo TITLE_TABLE_1; ?></h3>
				<table class="dtab">
					<tr>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
					</tr>

					<tr>
						<td>
							<img src="http://darwinne.github.io/FidoCadJ/images/logos/win_logo.png" alt="<?php echo WINDOWS_LOGO_ALT_ATTRIBUTE; ?>">
						</td>
						<td>
							<a href="<?php echo WINDOWS_VERSION_DOWNLOAD_LINK; ?>"><?php echo CURRENT_WINDOWS_VERSION." ".STABLE; ?></a>
						</td>
						<td>
							1&nbsp;MiB
						</td>
						<td>
							<?php echo WINDOWS_VERSION_DESCRIPTION; ?>
						</td>
					</tr>

					<tr>
						<td>
							<img src="http://darwinne.github.io/FidoCadJ/images/logos/macosx_logo.png" alt="<?php echo MAC_LOGO_ALT_ATTRIBUTE; ?>">
						</td>
						<td>
							<a href="<?php echo MAC_VERSION_DOWNLOAD_LINK; ?>"><?php echo CURRENT_MAC_VERSION." ".STABLE; ?></a>
						</td>
						<td>
							10&nbsp;MiB
						</td>
						<td>
							<?php echo MAC_VERSION_DESCRIPTION; ?>
						</td>
					</tr>

					<tr>
						<td>
							<img src="http://darwinne.github.io/FidoCadJ/images/logos/linux_logo.png" alt="<?php echo LINUX_LOGO_ALT_ATTRIBUTE; ?>">
						</td>
						<td>
							<a href="<?php echo LINUX_VERSION_DOWNLOAD_LINK; ?>"><?php echo CURRENT_LINUX_VERSION." ".STABLE; ?></a>
						</td>
						<td>
							640&nbsp;KiB
						</td>
						<td>
							<?php echo LINUX_VERSION_DESCRIPTION; ?>
							<span class="command">java -jar fidocadj.jar</span><br>
						</td>
					</tr>

					<tr>
						<td>
						</td>
						<td>
							<a href="https://github.com/DarwinNE/FidoCadJ/releases/download/v0.24.6/manual_en.pdf"><?php echo USER_MANUAL_NAME; ?></a><br>
						</td>
						<td>
							2.5&nbsp;MiB
						</td>
						<td>
							<?php echo USER_MANUAL_DESCRIPTION; ?>
						</td>
					</tr>
				</table>
				<p><?php echo TROUBLE_WITH_JAVA_TEXT; ?></p>

				<h3><?php echo TITLE_TABLE_2; ?></h3>
					<table class="dtab">
						<tr>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
						</tr>

						<tr>
							<td colspan="2">
								<a href="https://github.com/DarwinNE/FidoCadJ"><img src="http://darwinne.github.io/FidoCadJ/images/logos/GitHub-Mark-64px.png" alt="<?php echo GIT_HUB_LOGO_ALT_ATTRIBUTE; ?>"></a><br>
							</td>
							<td>
							</td>
							<td>
								<?php echo GIT_HUB_REPO_DESCRIPTION; ?>
							</td>
						</tr>

					<tr>
						<td>
							<img src="http://darwinne.github.io/FidoCadJ/images/logos/Android_Robot_100.png" alt="<?php echo ANDROID_LOGO_ALT_ATTRIBUTE; ?>" width="60">
						</td>
						<td>
							<a href="<?php echo ANDROID_VERSION_DOWNLOAD_LINK; ?>"><?php echo CURRENT_ANDROID_VERSION; ?></a>
						</td>
						<td>
							740&nbsp;KiB
						</td>
						<td>
							<?php echo ANDROID_VERSION_DESCRIPTION; ?>
						</td>
					</tr>
				</table>

				<h3><?php echo TITLE_TABLE_3; ?></h3>
				<p><?php echo SUBTITLE_TABLE_3; ?></p>

				<table class="dtab">
					<tr>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
					</tr>

					<tr>
						<td>
							<img src="http://darwinne.github.io/FidoCadJ/images/logos/win_logo.png" alt="<?php echo WINDOWS_LOGO_ALT_ATTRIBUTE; ?>">
						</td>
						<td>
							<a href="http://sourceforge.net/projects/fidocadj/files/versions/0.24.3/FidoCadJ_Windows.msi/download"><?php echo OLD_WINDOWS_STABLE_VERSION." ".STABLE; ?></a>
						</td>
						<td>
							0.8&nbsp;MiB
						</td>
						<td>
							<?php echo WINDOWS_OLD_STABLE_VERSION_DESCRIPTION; ?>
						</td>
					</tr>

				<tr>
					<td>
						<img src="http://darwinne.github.io/FidoCadJ/images/logos/macosx_logo.png" alt="<?php echo MAC_LOGO_ALT_ATTRIBUTE; ?>">
					</td>
					<td>
						<a href="http://sourceforge.net/projects/fidocadj/files/versions/0.24.3/FidoCadJ_MacOSX.dmg/download"><?php echo OLD_MAC_STABLE_VERSION." ".STABLE; ?></a>
					</td>
					<td>
						10&nbsp;MiB
					</td>
					<td>
						<?php echo MAC_OLD_STABLE_VERSION_DESCRIPTION; ?>
					</td>
				</tr>

				<tr>
					<td>
						<img src="http://darwinne.github.io/FidoCadJ/images/logos/linux_logo.png" alt="<?php echo LINUX_LOGO_ALT_ATTRIBUTE; ?>">
					</td>
					<td>
						<a href="http://sourceforge.net/projects/fidocadj/files/versions/0.24.3/fidocadj.jar/download"><?php echo OLD_LINUX_STABLE_VERSION." ".STABLE; ?></a>
					</td>
					<td>
						530&nbsp;KiB
					</td>
					<td>
						<?php echo LINUX_OLD_STABLE_VERSION_DESCRIPTION; ?><br> <span class="command">java -jar fidocadj.jar</span><br>
					</td>
				</tr>

				<tr>
					<td>
					</td>
					<td>
						<a href="http://sourceforge.net/projects/fidocadj/files/versions/0.24.3/manuals/fidocadj_manual_en.pdf/download"><?php echo USER_MANUAL_NAME_OLD_VERSION; ?></a><br>
					</td>
					<td>
						2.5&nbsp;MiB
					</td>
					<td>
						<?php echo USER_MANUAL_DESCRIPTION_OLD_VERSION; ?>
					</td>
				</tr>
			</table>
			<p><?php echo WARNING_GIT_HUB_MIGRATION; ?></p>
			<p><?php echo DOWNLOAD_PAGE_UPDATE_DATE; ?>	</p><br>
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
