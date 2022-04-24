package com.example.fashionshop.service.impl;

import com.example.fashionshop.model.Product;
import com.example.fashionshop.model.commons.Image;
import com.example.fashionshop.repository.ImageRepository;
import com.example.fashionshop.repository.ProductRepository;
import com.example.fashionshop.service.ImageService;
import com.example.fashionshop.service.ProductService;
import com.example.fashionshop.util.FileConstants;
import com.example.fashionshop.util.FileDatasource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ImageRepository imageRepository;

    /***
     *
     * @param productId finds the product that will be attached with image
     * @param images corresponding images that will be uploaded
     * @param serverUrl  current server where our page is hoisting
     * @return returns the product with images attached
     */
    @Override
    @Transactional
    public Product saveImagesToFolder(long productId, MultipartFile[] images, String serverUrl) {
// get product by id
        Product product = productService.getById(productId);
        List<Image> imagesForDb = new LinkedList<>();
// create directory
        FileDatasource fileDatasource = new FileDatasource();
        String productFolder = fileDatasource.createProductFolder(generateFolderName(product));

// iterate for any image
        for (MultipartFile image : images) {

            String fileName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
            Path uploadDirectory = Paths.get(productFolder);
            String imgUrl = serverUrl + "/" + generateFolderName(product) + "/" + fileName;
            imagesForDb.add(new Image(imgUrl));
            try (InputStream inputStream = image.getInputStream()) {
                Path filePath = uploadDirectory.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                try {
                    throw new IOException(" Error saving upload file" + fileName, e);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
        if (product.getImg() == null) {
            product.setImg(imagesForDb);
        } else {
            product.getImg().addAll(imagesForDb);
        }
        return product;
    }

    /***
     *
     * @param folderName is the name of the folder where the product is located
     * @param imageName is the name of the image
     * @return convert the file to an array of bytes and returns it
     * @throws IOException throws exception when the process has failed
     */
    @Override
    public byte[] readByFolderNameAndImageName(String folderName, String imageName) throws IOException {
        //get file
        File file = new File(
                new File("").getAbsolutePath() +
                        File.separator +
                        FileConstants.DATA_FOLDER_NAME +
                        File.separator +
                        folderName +
                        File.separator +
                        imageName
        );
        InputStream inputStream = new FileInputStream(file);
        return StreamUtils.copyToByteArray(inputStream);
    }

    /***
     *
     * @param productId finds the product which image will be updated
     * @param images the new images that will be uploaded
     * @param serverUrl current server where our page is hoisting
     * @return
     */
    @Override
    @Transactional
    public Image update(long productId, MultipartFile[] images, String serverUrl) {
        Product fromDb=productService.getById(productId);
        FileDatasource fileDatasource = new FileDatasource();
        fileDatasource.deleteProductFolderByFolderName(generateFolderName(fromDb));
        String productFolder = fileDatasource.createProductFolder(generateFolderName(fromDb));
        List<Image> img = fromDb.getImg();
        for (Image image : img) {
            imageRepository.deleteById(image.getId());
        }
        fromDb.setImg(new LinkedList<>());

        List<Image> imagesForDb = new LinkedList<>();
// create directory

// iterate for any image
        for (MultipartFile image : images) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
            Path uploadDirectory = Paths.get(productFolder);
            String imgUrl  = serverUrl + "/" + generateFolderName(fromDb) + "/" + fileName;;
            imagesForDb.add(new Image(imgUrl));
            try (InputStream inputStream = image.getInputStream()) {
                Path filePath = uploadDirectory.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                try {
                    throw new IOException(" Error saving upload file" + fileName, e);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
        fromDb.getImg().addAll(imagesForDb);
        return null;
    }

    /***
     *
     * @param id find the product with provided id and deletes both the image folder
     *           corresponding to the product and the product
     */
    @Override
    public void delete(long id) {
        new FileDatasource().deleteProductFolderByFolderName(generateFolderName(productRepository.getById(id)));
    }

    /***
     *
     * @param product creates the image folder depending on the provided product name and product id
     * @return generated folder name
     */
    private String generateFolderName(Product product) {
        return product.getName() + "_" + product.getId();
    }
}
