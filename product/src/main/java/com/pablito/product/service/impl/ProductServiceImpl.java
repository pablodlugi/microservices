package com.pablito.product.service.impl;

import com.pablito.product.config.properties.FilePropertiesConfig;
import com.pablito.product.domain.dao.Product;
import com.pablito.product.helper.FileHelper;
import com.pablito.product.repository.ProductRepository;
import com.pablito.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final FilePropertiesConfig filePropertiesConfig;
    private final ProductRepository productRepository;
    private final FileHelper fileHelper;

    @Override
    public Product save(Product product, MultipartFile image) {
        pathFill(product, image, null);
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product update(Product product, Long id, MultipartFile image) {
        var productDb = getProductById(id);
        productDb.setName(product.getName());
        productDb.setCode(product.getCode());
        productDb.setPrice(product.getPrice());
        productDb.setQuantity(product.getQuantity());

        String oldImagePath = productDb.getImagePath();

        pathFill(productDb, image, oldImagePath);

        return productDb;
    }

    private void pathFill(Product product, MultipartFile image, String oldImagePath) {
        var path = Paths.get(filePropertiesConfig.getProduct(), product.getName() + "."
                + FilenameUtils.getExtension(image.getOriginalFilename()));
        try {
            fileHelper.saveFile(image.getInputStream(), path);
            product.setImagePath(path.toString());

            if (!product.getImagePath().equals(oldImagePath)) {
                fileHelper.deleteFile(oldImagePath);
            }
        } catch (Exception e) {
            log.error("Image couldn't upload properly", e);
        }
    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public Product getProductById(Long id) {
        log.info("Product not in cache {}", id);
        return productRepository.getById(id);
    }

    @Override
    public Page<Product> getPage(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
}
