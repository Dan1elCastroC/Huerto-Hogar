export const addItemToCart = (cart, product) => {
  const existing = cart.find((item) => item.id === product.id);

  if (existing) {
    return cart.map((item) => (
      item.id === product.id
        ? { ...item, cantidad: Math.min(item.cantidad + 1, item.stock ?? Number.MAX_SAFE_INTEGER) }
        : item
    ));
  }

  return [...cart, { ...product, cantidad: 1 }];
};

export const updateCartItem = (cart, productId, cantidad) => (
  cantidad <= 0
    ? cart.filter((item) => item.id !== productId)
    : cart.map((item) => (
      item.id === productId
        ? { ...item, cantidad: Math.min(cantidad, item.stock ?? cantidad) }
        : item
    ))
);

export const removeCartItem = (cart, productId) => (
  cart.filter((item) => item.id !== productId)
);

export const getCartTotal = (cart) => (
  cart.reduce((total, item) => total + (Number(item.precio) * item.cantidad), 0)
);
