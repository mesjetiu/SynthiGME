+ S100_GUI {
	makePannel1 {|parent|
		var rect = Rect(
			0,
			0,
			rectWindow.width/4,
			rectWindow.width/4,
		);

		var imagePannel1 = Image(installedPath ++ "/classes/GUI/images/pannel_1.png");
		var compositeView = CompositeView(parent, rect).setBackgroundImage(imagePannel1,10);

		defaultSizes = defaultSizes.add([compositeView, compositeView.bounds]);
	}
}