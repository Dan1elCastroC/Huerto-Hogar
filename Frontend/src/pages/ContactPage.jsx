import ContactoForm from '../components/contact/ContactoForm';

export default function ContactPage({ user }) {
  return (
    <main className="container my-5" style={{ maxWidth: 760 }}>
      <div className="seccion-titulo">
        <h1>Contáctanos</h1>
        <p>Envíanos tus consultas, sugerencias o comentarios.</p>
      </div>
      <ContactoForm user={user} />
    </main>
  );
}
