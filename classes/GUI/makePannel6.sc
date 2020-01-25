+ S100_GUI {
	makePannel6 {|parent|
		var rect = Rect(
			(rectWindow.width/4) * 2,
			rectWindow.width/4,
			rectWindow.width/4,
			rectWindow.width/4,
		);

		var imagePannel6 = Image(installedPath ++ "/classes/GUI/images/pannel_5.png");
		var compositeView = CompositeView(parent, rect).setBackgroundImage(imagePannel6,10);
		compositeView.background = whiteBackground;


		defaultSizes = defaultSizes.add([compositeView, compositeView.bounds]);
	}
}