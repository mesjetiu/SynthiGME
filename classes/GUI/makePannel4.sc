+ S100_GUI {
	makePannel4 {|parent|
		var rect = Rect(
			(rectWindow.width/4) * 3,
			0,
			rectWindow.width/4,
			rectWindow.width/4,
		);

		var imagePannel4 = Image(installedPath ++ "/classes/GUI/images/pannel_4.png");
		var compositeView = CompositeView(parent, rect).setBackgroundImage(imagePannel4,10);

		defaultSizes = defaultSizes.add([compositeView, compositeView.bounds]);
	}
}