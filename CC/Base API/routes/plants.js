const plants = require('../data/plants');
const products = require('../data/products');
const plantDiseases = require('../data/plant-diseases');
const articles = require('../data/articles');

const getAllPlants = (request, h) => {
  return plants;
};

const getPlantByName = (request, h) => {
  const plantName = request.params.name.toLowerCase();
  const plant = plants.find(p => p.name.toLowerCase() === plantName);

  if (!plant) {
    return h.response({ message: 'Plant disease not found' }).code(404);
  }

  return plant;
};

const getAllProducts = (request, h) => {
  return products;
};

const getProductsByName = (request, h) => {
  const productName = request.params.name.toLowerCase();
  const product = products.find(p => p.name.toLowerCase() === productName);

  if (!product) {
    return h.response({ message: 'Product not found' }).code(404);
  }

  return product;
};

const getAllPlantDiseases = (request, h) => {
  return plantDiseases;
};

const getPlantDiseasesByName = (request, h) => {
  const plantDiseaseName = request.params.name.toLowerCase();
  const plantDisease = plantDiseases.find(p => p.name.toLowerCase() === plantDiseaseName);

  if (!plantDisease) {
    return h.response({ message: 'Pest not found' }).code(404);
  }

  return plantDisease;
};
const getAllArticles = (request, h) => {
  return articles;
};

const getArticleByName = (request, h) => {
  const articleName = request.params.name.toLowerCase();
  const article = articles.find(p => p.name.toLowerCase() === articleName);

  if (!article) {
    return h.response({ message: 'Article not found' }).code(404);
  }

  return article;
};

module.exports = [
  {
    method: 'GET',
    path: '/plants',
    handler: getAllPlants
  },
  {
    method: 'GET',
    path: '/plants/{name}',
    handler: getPlantByName
  },
  {
  method: 'GET',
    path: '/products',
    handler: getAllProducts
  },
  {
    method: 'GET',
    path: '/products/{name}',
    handler: getProductsByName
  },
  {
    method: 'GET',
    path: '/pest',
    handler: getAllPlantDiseases
  },
  {
    method: 'GET',
    path: '/pest/{name}',
    handler: getPlantDiseasesByName
  },
  {
    method: 'GET',
    path: '/articles',
    handler: getAllArticles
  },
  {
    method: 'GET',
    path: '/articles/{name}',
    handler: getArticleByName
  },
];
