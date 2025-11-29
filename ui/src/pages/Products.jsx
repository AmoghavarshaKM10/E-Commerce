import React, { useEffect, useState } from 'react'
import apiClient from '../api/axios'
import { apiUrl } from '../api/api'
import { Service } from '../api/Service'

const Products = () => {
  const [products, setProducts] = useState([])
  const [hoveredId, setHoveredId] = useState(null)
  const [descriptions, setDescriptions] = useState({}) // store description per product

  // Fetch all products on page load
  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const { data } = await apiClient.get(apiUrl(Service.PRODUCTS, '/all'))
        setProducts(data)
      } catch (err) {
        console.error('Failed to fetch products', err)
      }
    }
    fetchProducts()
  }, [])

  // Fetch description when hovered
  const fetchDescription = async (productId) => {
    if (descriptions[productId]) return; // already fetched

    try {
      const { data } = await apiClient.get(
        apiUrl(Service.PRODUCTS, `/${productId}/description`)
      )
      setDescriptions((prev) => ({
        ...prev,
        [productId]: { ...pFromProducts(productId), ...data } // merge with original product
      }))
    } catch (err) {
      console.error(`Failed to fetch description for product ${productId}`, err)
    }
  }

  const pFromProducts = (id) => products.find((p) => p.productId === id) || {}

  const handleAdd = async (product) => {
  try {
    const payload = {
      productId: product.productId,
      productName: product.name,
      quantity: 1,
      unitPrice: product.price
    }
      const response = await apiClient.post(
      apiUrl(Service.CART, '/items'),
      payload   // <-- DO NOT wrap inside { payload }
    );
    
    // Access HTTP status code
    //console.log('Status:', response.status) // e.g., 200, 201
    
    // Access response data if needed
    //console.log('Data:', response.data)
    
    if (response.status === 200) {
     alert(`Product ${product.name} added to cart successfully!`)
    } else {
      alert(`Failed to add product ${product.name} to cart.`)
    }
  } catch (err) {
    console.error('Failed to add to cart', err)
    // Axios errors have a response property
    if (err.response) {
      console.log('Error status:', err.response.status)
      console.log('Error data:', err.response.data)
    }
  }
}

  return (
    <div className="page">
      <div className="page-header">
        <h2>Our Products</h2>
        <p>Discover amazing products at great prices</p>
      </div>
      <div className="products-grid">
        {products.map((p) => (
          <div
            key={p.productId}
            className="product-card"
            onMouseEnter={() => fetchDescription(p.productId)}
          >
            <img src={p.imageUrl} alt={p.name} className="product-image" />
            <h3 className="product-name">{p.name}</h3>
            <div className="product-price">${p.price}</div>

            {/* Hover overlay */}
            <div className="product-hover-info">
              <p>{descriptions[p.productId]?.description || p.description}</p>

              <p>
                <strong>Warranty:</strong>{" "}
                {descriptions[p.productId]?.warrantyPeriod
                  ? `${descriptions[p.productId].warrantyPeriod} months`
                  : `${p.warrantyPeriod} months`}
              </p>

              <p>
                <strong>Return:</strong>{" "}
                {descriptions[p.productId]?.returnPeriod
                  ? `${descriptions[p.productId].returnPeriod} days`
                  : `${p.returnPeriod} days`}
              </p>

              <p className="rating">
                ⭐{" "}
                {descriptions[p.productId]?.ratings ?? p.ratings}{" "}
                <span>
                  ({descriptions[p.productId]?.reviewsCount ?? p.reviewsCount} reviews)
                </span>
              </p>
            </div>


            {/* Add to Cart button */}
            <button className="btn add-btn" onClick={() => handleAdd(p)}>
              Add to Cart
            </button>
          </div>
        ))}
      </div>
    </div>
  )
}

export default Products
