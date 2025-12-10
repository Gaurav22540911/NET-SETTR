package com.NET_SETTR.NET_SETTR.service;

import com.NET_SETTR.NET_SETTR.model.Slide;
import com.NET_SETTR.NET_SETTR.repository.SlideRepository;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

@Service
public class PptConversionService {

    private final SlideRepository slideRepository;

    public PptConversionService(SlideRepository slideRepository) {
        this.slideRepository = slideRepository;
    }

    public int convertPptToImages(Integer courseId, String pptPath) throws Exception {

        FileInputStream inputStream = new FileInputStream(pptPath);
        XMLSlideShow ppt = new XMLSlideShow(inputStream);
        List<XSLFSlide> slides = ppt.getSlides();

        String outputFolder = pptPath.substring(0, pptPath.lastIndexOf(".")) + "/slides";
        File folder = new File(outputFolder);
        folder.mkdirs();

        int slideNumber = 1;

        for (XSLFSlide slide : slides) {

            Dimension pgsize = ppt.getPageSize();
            BufferedImage img = new BufferedImage(pgsize.width, pgsize.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = img.createGraphics();

            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            slide.draw(graphics);
            graphics.dispose();

            String outputImgPath = outputFolder + "/" + slideNumber + ".png";
            ImageIO.write(img, "png", new File(outputImgPath));

            // Save slide in DB
            Slide s = new Slide();
            s.setCourseId(courseId);
            s.setSlideNumber(slideNumber);
            s.setSlideTitle("Slide " + slideNumber);
            s.setImagePath(outputImgPath);
            s.setFilePath(outputImgPath);
            s.setDisplayOrder(slideNumber);
            s.setActive(true);

            slideRepository.save(s);

            slideNumber++;
        }

        return slides.size();
    }
}
