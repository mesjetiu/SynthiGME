+ S100_GUI {
	makePannel2 {|parent|
		var rect = Rect(
			(rectWindow.width/4),
			0,
			rectWindow.width/4,
			rectWindow.width/4,
		);

		var imagePannel2 = Image(installedPath ++ "/classes/GUI/images/pannel_2.png");
		var compositeView = CompositeView(parent, rect).setBackgroundImage(imagePannel2,10);

		defaultSizes = defaultSizes.add([compositeView, compositeView.bounds]);
	}
}