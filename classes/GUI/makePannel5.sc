+ S100_GUI {
	makePannel5 {|parent|
		var rect = Rect(
			0,
			rectWindow.width/4,
			rectWindow.width/4,
			rectWindow.width/4,
		);

		var imagePannel5 = Image(installedPath ++ "/classes/GUI/images/pannel_5.png");
		var compositeView = CompositeView(parent, rect).setBackgroundImage(imagePannel5,10);
		compositeView.background = whiteBackground;


		defaultSizes = defaultSizes.add([compositeView, compositeView.bounds]);
	}
}