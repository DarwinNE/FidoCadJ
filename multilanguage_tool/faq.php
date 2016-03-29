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
	include_once("languages/".$language."/faq.php");
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
				<a href="https://twitter.com/share?via=davbucci" class="twitter-share-button">Tweet</a>
				<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+'://platform.twitter.com/widgets.js';fjs.parentNode.insertBefore(js,fjs);}}(document, 'script', 'twitter-wjs');</script>
				<a href="index.html" class="mainmenu">[<?php echo HOME_BUTTON; ?>]</a>
				&nbsp;
				<a href="download.html" class="mainmenu">[<?php echo DOWNLOAD_BUTTON; ?>]</a>
				&nbsp;
				<a href="scrn.html" class="mainmenu">[<?php echo SCREENSHOTS_BUTTON; ?>]</a>
				&nbsp;
				<a href="libs.html" class="mainmenu">[<?php echo LIBRARIES_BUTTON; ?>]</a>
				&nbsp;
				<b class="mainmenu">[<?php echo FAQ_BUTTON; ?>]</b>
				&nbsp;
				<a href="examples.html" class="mainmenu">[<?php echo EXAMPLES_BUTTON; ?>]</a>
				&nbsp;
				<a href="<?php echo "http://darwinne.github.io/FidoCadJ/".str_replace(".php",".html",basename(__FILE__)); ?>"><img src="../images/flags/english_flag.jpg" alt="<?php echo ENGLISH_FLAG_IMAGE_ALT; ?>"/></a>
				&nbsp;
				<a href="<?php echo "http://darwinne.github.io/FidoCadJ/lang/it/".str_replace(".php",".html",basename(__FILE__)); ?>"> <img src="../images/flags/italian_flag.jpg" alt="<?php echo ITALIAN_FLAG_IMAGE_ALT; ?>"/></a>
		</header>

		<div id="content">
			<h3><?php echo PAGE_SUB_TITLE; ?></h3>
			<ul id="faq_index">
				<li><a href="#1"><?php echo FAQ_INDEX_1; ?></a></li>
				<li><a href="#2"><?php echo FAQ_INDEX_2; ?></a></li>
				<li><a href="#3"><?php echo FAQ_INDEX_3; ?></a></li>
				<li><a href="#4"><?php echo FAQ_INDEX_4; ?></a></li>
				<li><a href="#5"><?php echo FAQ_INDEX_5; ?></a></li>
				<li><a href="#6"><?php echo FAQ_INDEX_6; ?></a></li>
				<li><a href="#7"><?php echo FAQ_INDEX_7; ?></a></li>
				<li><a href="#8"><?php echo FAQ_INDEX_8; ?></a></li>
				<li><a href="#9"><?php echo FAQ_INDEX_9; ?></a></li>
				<li><a href="#10"><?php echo FAQ_INDEX_10; ?></a></li>
			</ul>

			<h4><a class="faq_title" name="1"><?php echo FAQ_TITLE_1; ?></a></h4>
			<?php echo FAQ_CONTENT_1; ?>
			<p class="go_top_label"><a href="#top">top</a></p>
			<h4><a class="faq_title" name="2"><?php echo FAQ_TITLE_2; ?></a></h4>
			<?php echo FAQ_CONTENT_2; ?>
			<p class="go_top_label"><a href="#top">top</a></p>
			<h4><a class="faq_title" name="3"><?php echo FAQ_TITLE_3; ?></a></h4>
			<?php echo FAQ_CONTENT_3; ?>
			<p class="go_top_label"><a href="#top">top</a></p>
			<h4><a class="faq_title" name="4"><?php echo FAQ_TITLE_4; ?></a></h4>
			<?php echo FAQ_CONTENT_4; ?>
			<p class="go_top_label"><a href="#top">top</a></p>
			<h4><a class="faq_title" name="5"><?php echo FAQ_TITLE_5; ?></a></h4>
			<?php echo FAQ_CONTENT_5; ?>
			<p class="go_top_label"><a href="#top">top</a></p>
			<h4><a class="faq_title" name="6"><?php echo FAQ_TITLE_6; ?></a></h4>
			<?php echo FAQ_CONTENT_6; ?>
			<p class="go_top_label"><a href="#top">top</a></p>
			<h4><a class="faq_title" name="7"><?php echo FAQ_TITLE_7; ?></a></h4>
			<?php echo FAQ_CONTENT_7; ?>
			<h4><a class="faq_title" name="8"><?php echo FAQ_TITLE_8; ?></a></h4>
			<?php echo FAQ_CONTENT_8; ?>
			<p class="go_top_label"><a href="#top">top</a></p>
			<h4><a class="faq_title" name="9"><?php echo FAQ_TITLE_9; ?></a></h4>
			<?php echo FAQ_CONTENT_9; ?>
			<p class="go_top_label"><a href="#top">top</a></p>
			<h4><a class="faq_title" name="10"><?php echo FAQ_TITLE_10; ?></a></h4>
			<?php echo FAQ_CONTENT_10; ?>
			<p class="go_top_label"><a href="#top">top</a></p>

			<p><?php echo FAQ_PAGE_UPDATE_DATE; ?></p><br>
		</div>
<!-- end of content -->
		<footer>
			<h3><?php echo FOOTER_TITLE; ?></h3>
			<img id="gplv3_logo" src="http://darwinne.github.io/FidoCadJ/images/logos/gplv3-127x51.png" alt="GPL v. 3">
			<p id="license"><?php echo FOOTER_CONTENT; ?></p>
		</footer>
	</body>
</html>
