package net.minecraft.client.resources;

public interface ClientSideDatapack {

	/**
	 * Инициализация датапака на стороне клиента.
	 * Унаследуйтесь от этого класса, если ваш датапак затрагивает клиентскую часть
	 */
	void clientInit(ClientRegistrar registrar);

}
