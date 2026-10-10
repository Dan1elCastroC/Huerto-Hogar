// src/components/LandingPage.jsx
import React from 'react';

export default function LandingPage({ cambiarVista }) {
    return (
        <div>
            {/* Hero Section */}
            <div className="bg-success text-white py-5 text-center">
                <div className="container py-5">
                    <h1 className="display-4 fw-bold mb-3">Cultiva tu propio espacio natural</h1>
                    <p className="lead mb-4 col-md-8 mx-auto">
                        Encuentra todo lo necesario para tu huerto urbano, compostaje doméstico y herramientas sustentables directamente en la puerta de tu hogar.
                    </p>
                    <button className="btn btn-light btn-lg text-success fw-bold px-5 shadow" onClick={() => cambiarVista('catalogo')}>
                        Ver Catálogo de Productos
                    </button>
                </div>
            </div>

            {/* Features Section */}
            <div className="container my-5 py-4">
                <div className="row text-center">
                    <div className="col-md-4 mb-4">
                        <div className="card h-100 border-0 shadow-sm p-4">
                            <div className="fs-1 mb-3">🌿</div>
                            <h4 className="fw-bold text-success">100% Orgánico</h4>
                            <p className="text-muted">Insumos certificados y amigables con el medio ambiente para potenciar tus cosechas.</p>
                        </div>
                    </div>
                    <div className="col-md-4 mb-4">
                        <div className="card h-100 border-0 shadow-sm p-4">
                            <div className="fs-1 mb-3">🚚</div>
                            <h4 className="fw-bold text-success">Envíos Rápidos</h4>
                            <p className="text-muted">Despachos eficientes a lo largo de todo el país con cobertura garantizada.</p>
                        </div>
                    </div>
                    <div className="col-md-4 mb-4">
                        <div className="card h-100 border-0 shadow-sm p-4">
                            <div className="fs-1 mb-3">🔒</div>
                            <h4 className="fw-bold text-success">Compra Segura</h4>
                            <p className="text-muted">Plataforma robusta conectada de manera segura con sistemas transaccionales.</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}