package tests;

import com.github.javafaker.Faker;
import okhttp3.ResponseBody;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofitGB.dto.Category;
import retrofitGB.dto.Product;
import retrofitGB.enums.CategoryType;
import retrofitGB.service.CategoryService;
import retrofitGB.service.ProductService;
import retrofitGB.utils.PrettyLogger;
import retrofitGB.utils.RetrofitUtils;

import java.io.IOException;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class ProductTests {
    static Retrofit client;
    static ProductService productService;
    static CategoryService categoryService;
    Product product;
    Faker faker = new Faker();
    PrettyLogger prettyLogger = new PrettyLogger();
    Integer id;

    @BeforeAll
    static void beforeAll() {
        client = RetrofitUtils.getRetrofit();
        productService = client.create(ProductService.class);
        categoryService = client.create(CategoryService.class);
    }

    @BeforeEach
    void setUp() {
        product = new Product()
                .withTitle(faker.food().fruit())
                .withCategoryTitle(CategoryType.FOOD.getTitle())
                .withPrice((int) ((Math.random() + 1) * 100));
    }

    @Test
    void createProductTest() throws IOException {
        Response<Product> response = productService.createProduct(product).execute();
        prettyLogger.log(response.body().toString());
        assertThat(response.code(), equalTo(201));
        assertThat(response.body().getTitle(), equalTo(product.getTitle()));
        assertThat(response.body().getPrice(), equalTo(product.getPrice()));
        assertThat(response.body().getCategoryTitle(), equalTo(product.getCategoryTitle()));
    }

    @Test
    void createProductWithIdTest() throws IOException {
        product.setId(13239);
        Response<Product> response = productService.createProduct(product).execute();
        assertThat(response.code(), equalTo(400));
    }

    @Test
    void createProductWithNullFieldTest() throws IOException {
        product.setPrice(null);
        product.setTitle(null);
        Response<Product> response = productService.createProduct(product).execute();
        assertThat(response.code(), equalTo(400));
    }

    @Test
    void createProductWithOtherCategoryTest() throws IOException {
        product.setCategoryTitle("Other");
        Response<Product> response = productService.createProduct(product).execute();
        assertThat(response.code(), equalTo(201));
        if (response.code() == 201) prettyLogger.log(response.body().toString());
    }

    @Test
    void getProductsByIdTest() throws IOException {
        Response<Product> response = productService.createProduct(product).execute();
        id = response.body().getId();
        response = productService
                .getProduct(id)
                .execute();
        prettyLogger.log(response.body().toString());
        assertThat(response.code(), equalTo(200));
        assertThat(response.body().getId(), equalTo(id));
        assertThat(response.body().getTitle(), equalTo(product.getTitle()));
        assertThat(response.body().getPrice(), equalTo(product.getPrice()));
        assertThat(response.body().getCategoryTitle(), equalTo(product.getCategoryTitle()));
    }

    @Test
    void getAfterDeleteProductTest() throws IOException {
        Response<Product> response = productService.createProduct(product).execute();
        id = response.body().getId();
        Response<ResponseBody> responseDel = productService
                .deleteProduct(id)
                .execute();
        response = productService
                .getProduct(id)
                .execute();
        assertThat(response.code(), equalTo(404));
    }

    @Test
    void getProductByNonexistentIdTest() throws IOException {
        Response<Product> response = productService
                .getProduct(-1)
                .execute();
        assertThat(response.code(), equalTo(404));
    }

    @Test
    void getAllProductsTest() throws IOException {
        Response<ArrayList<Product>> response = productService
                .getProducts()
                .execute();
        prettyLogger.log(response.body().toString());
        for (int i = 0; i < response.body().size(); i++) {
            assertThat(response.code(), equalTo(200));
            assertThat(response.body().get(i).getId().toString(), Matchers.matchesPattern("^[0-9]{1,5}"));
            assertThat(response.body().get(i).getTitle(), Matchers.is(notNullValue()));
            assertThat(response.body().get(i).getPrice(), Matchers.is(notNullValue()));
            assertThat(response.body().get(i).getCategoryTitle(), Matchers.is(notNullValue()));
        }
    }

    @Test
    void getAfterDeleteAllProductsTest() throws IOException {
        Response<ArrayList<Product>> response = productService
                .getProducts()
                .execute();
        Integer currentId;
        Response<ResponseBody> responseDel;
        for (int i = 0; i < response.body().size(); i++) {
            currentId = response.body().get(i).getId();
            responseDel = productService
                    .deleteProduct(currentId)
                    .execute();
        }
        response = productService
                .getProducts()
                .execute();
        prettyLogger.log(response.body().toString());
        assertThat(response.code(), equalTo(200));
    }

    @Test
    void updateProductTest() throws IOException {
        Response<Product> response = productService
                .createProduct(product)
                .execute();
        id = response.body().getId();
        Integer price = 1500;
        String title = "New title";
        String category = "Furniture";
        product.setId(id);
        product.setPrice(price);
        product.setTitle(title);
        product.setCategoryTitle(category);
        Response<Product> responseUpd = productService
                .updateProduct(product)
                .execute();
        prettyLogger.log(responseUpd.body().toString());
        assertThat(responseUpd.code(), equalTo(200));
        assertThat(responseUpd.body().getPrice(), equalTo(price));
        assertThat(responseUpd.body().getTitle(), equalTo(title));
        assertThat(responseUpd.body().getCategoryTitle(), equalTo(category));
    }

    @Test
    void updateProductWithoutIdTest() throws IOException {
        Response<Product> response = productService
                .createProduct(product)
                .execute();
        id = response.body().getId();
        Integer price = 1500;
        String title = "New title";
        String category = "Furniture";
        product.setPrice(price);
        product.setTitle(title);
        product.setCategoryTitle(category);
        Response<Product> responseUpd = productService
                .updateProduct(product)
                .execute();
        assertThat(responseUpd.code(), equalTo(400));
    }

    @Test
    void updateWithNullFieldTest() throws IOException {
        Response<Product> response = productService
                .createProduct(product)
                .execute();
        id = response.body().getId();
        Integer price = null;
        String title = null;
        product.setId(id);
        product.setPrice(price);
        product.setTitle(title);
        Response<Product> responseUpd = productService
                .updateProduct(product)
                .execute();
        prettyLogger.log(responseUpd.body().toString());
        assertThat(responseUpd.code(), equalTo(400));
    }

    @Test
    void updateProductAfterDeleteTest() throws IOException {
        Response<Product> response = productService
                .createProduct(product)
                .execute();
        id = response.body().getId();
        Response<ResponseBody> responseDel = productService
                .deleteProduct(id)
                .execute();
        Integer price = 1500;
        String title = "New title";
        String category = "Furniture";
        product.setId(id);
        product.setPrice(price);
        product.setTitle(title);
        product.setCategoryTitle(category);
        System.out.println(product);
        Response<Product> responseUpd = productService
                .updateProduct(product)
                .execute();
        assertThat(responseUpd.code(), equalTo(404));
    }

    @Test
    void deleteProductTest() throws IOException {
        Response<Product> response = productService.createProduct(product).execute();
        id = response.body().getId();
        Response<ResponseBody> responseDel = productService
                .deleteProduct(id)
                .execute();
        assertThat(responseDel.code(), equalTo(200));
    }

    @Test
    void deleteAfterDeleteProductTest() throws IOException {
        Response<Product> response = productService.createProduct(product).execute();
        id = response.body().getId();
        Response<ResponseBody> responseDel = productService
                .deleteProduct(id)
                .execute();
        responseDel = productService
                .deleteProduct(id)
                .execute();
        assertThat(responseDel.code(), equalTo(404));
    }

    @Test
    void getCategoryByIdTest() throws IOException {
        Integer idCategory = CategoryType.FOOD.getId();
        Response<Category> response = categoryService
                .getCategory(idCategory)
                .execute();
        prettyLogger.log(response.body().toString());
        assertThat(response.body().getTitle(), equalTo(CategoryType.FOOD.getTitle()));
        assertThat(response.body().getId(), equalTo(idCategory));
    }
}
